from emoji import core
import re
from ckonlpy.tag import Twitter
from sklearn.metrics.pairwise import cosine_similarity

import json, csv, boto3, yaml, io

with open('s3config.yaml', 'r') as f:
    config = yaml.safe_load(f)

s3_client = boto3.client(
    service_name=config['service_name'],
    region_name=config['region_name'],
    aws_access_key_id=config['access_key'],
    aws_secret_access_key=config['secret_key']
)
bucket_name = config['bucket_name']


def extract_keywords(model, predefined_embeddings, sentence):
    try:
        response = s3_client.get_object(Bucket=bucket_name, Key="data/taglist.csv")
        reader = csv.reader(io.StringIO(response["Body"].read().decode('cp949')))
        tag_list = next(reader)
        
    except Exception as e:
        print(f"Error: {e}")
    tokenizer = Twitter()
    tokenizer.add_dictionary(tag_list, 'Noun')
    tokenizer.add_dictionary(["플레이리스트", "집중력", "릴렉스", "고즈넉"], 'Noun')
    # 미리 정해진 키워드의 BERT 임베딩 계산
    
    stopwords = [
    "이", "가", "은", "는", "을", "를", "의", "에", "에서", "와", "과", "도", "로", "으로", "하고",
    "나", "다", "라", "의거", "에게", "뿐", "만", "조차", "까지", "에서부터", "으로부터", "까지만",
    "그리고", "그러나", "하지만", "그렇지만", "그래서", "그러므로", "따라서", "그렇기 때문에",
    "만약", "만일", "비록", "뿐만 아니라", "혹은", "또는", "만약에", "하여", "나", "너", "그",
    "그녀", "저", "이", "그", "저", "우리", "너희", "그들", "것", "데", "수", "사람", "경우",
    "때", "아주", "매우", "너무", "더", "가장", "거의", "약간", "조금", "상당히", "정말",
    "진짜", "참", "자주", "항상", "또", "다시", "언제나", "이미", "아직", "곧", "방금", "지금",
    "현재", "일단", "당장", "즉시", "곧바로", "이것", "저것", "그것", "여기", "저기", "거기", "플레이리스트",
    "노래", "곡", "듣기", "좋은", "어울리는", "할", "때", "듣는", "했을", "지고", "음악", "나올", "트는", "추천", "찬", "면서",
    "앞", "고픈", "운", "시간", "가득", "선율", "여주", "높", "뮤직", "충만", "날", "위", "히트","모음",
    "후", "업", "필요", "순간", "후의", "타임", "멜로디", "속", "기분", "여", "명곡", "물씬", "적", "온", "소리", "트랙", "취향", "저격", "송"
    ]
    
    sentence = core.replace_emoji(sentence, replace="")
    sentence = re.sub("[^ㄱ-ㅎ ㅏ-ㅣ가-힣 1-9]", "", sentence)  # 정규 표현식 수행
    sentence = re.sub('^ +', "", sentence)  # 공백은 empty 값으로 변경
    tokenized_sentence = tokenizer.pos(sentence)

    # 명사, 동사, 형용사 추출
    stopwords_removed_sentence = [word for word in tokenized_sentence if not word[0] in stopwords]  # 불용어 제거
    keywords = [word for word, pos in stopwords_removed_sentence if pos in ['Noun', 'Adjective']]

    mapped_keywords = []
    for keyword in keywords:
        keyword_embedding = model.encode(keyword)
        similarities = {kw: cosine_similarity([keyword_embedding], [emb]).item() for kw, emb in predefined_embeddings.items()}
        max_similarity = max(similarities.values())
        if max_similarity > 0.6:
            max_similarity_keyword = max(similarities, key=similarities.get)
            mapped_keywords.append(max_similarity_keyword)
    return mapped_keywords

def kor_to_en (keywords):
    try:
        print(keywords)
        response = s3_client.get_object(Bucket=bucket_name, Key="data/tag_mapping.json")
        print(response)
        translation_dict = json.load(io.StringIO(response["Body"].read().decode('utf-8')))
        print("JSON 파일을 성공적으로 읽었습니다.")   
    except Exception as e:
        print(f"Error: {e}")
    # with open(r"resources\static\data\tag_mapping.json", 'r', encoding='utf-8') as file:
    #     translation_dict = json.load(file)
    
    english_tags = []
    for tag in keywords:
        translated_tag = translation_dict.get(tag)
        if translated_tag:
            english_tags.append(translated_tag)
        else:
            english_tags.append(f"[{tag}]")  # Translation not found, keep the original tag
    return english_tags