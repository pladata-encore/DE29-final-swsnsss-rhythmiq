import pandas as pd
from collections import Counter
import random
from tqdm import tqdm

from sklearn.neighbors import NearestNeighbors
import time, io, boto3, yaml, itertools

with open('s3config.yaml', 'r') as f:
    config = yaml.safe_load(f)

s3_client = boto3.client(
    service_name=config['service_name'],
    region_name=config['region_name'],
    aws_access_key_id=config['access_key'],
    aws_secret_access_key=config['secret_key']
)
bucket_name = config['bucket_name']

def parse_str_list(str_list):
    if isinstance(str_list, list):
        return str_list
    try:
        return eval(str_list)
    except:
        return []

def filter_playlists(data, tag_keywords):
    tag_set = set(tag_keywords)
    return [playlist for playlist in data if tag_set.issubset(set(parse_str_list(playlist['tags'])))]

def melon_to_spotify(melon_id_list):
    try:
        mtos = s3_client.get_object(Bucket=bucket_name, Key="data/melontospotify.csv")
        df_mapping = pd.read_csv(io.BytesIO(mtos["Body"].read()))
    except Exception as e:
        print(f"Error: {e}")
    df_mapping = df_mapping[df_mapping['spotify_id'] != "null"]
    df_mapping['melon_id'] = df_mapping['melon_id'].astype(str)
    melon_ids = pd.Series(melon_id_list, name='melon_id').astype(str)
    merged = melon_ids.to_frame().merge(df_mapping, on='melon_id', how='left')
    return merged['spotify_id'].dropna().tolist()


def apply_knn_songs(feature_matrix, target_song_indices, n_neighbors=5):
    if not target_song_indices:
        return []
    
    model = NearestNeighbors(n_neighbors=n_neighbors, metric='cosine', algorithm='brute')
    model.fit(feature_matrix.T)
    _, indices = model.kneighbors(feature_matrix.T[target_song_indices])
    return indices.flatten()

def generate_tag_combinations(tags):
    tag_combinations = []
    for i in range(len(tags), 0, -1):
        tag_combinations.extend(list(itertools.combinations(tags, i)))
    return tag_combinations

def main_recommend_with_knn(input_tags, num_songs, feature_matrix, song_index, train_data):
    
    
    half_num_songs = num_songs // 2
    tag_combinations = generate_tag_combinations(input_tags)
    tagged_playlists = []
    for tags in tag_combinations:
        tagged_playlists = filter_playlists(train_data, tags)
        if tagged_playlists:
            break
    if not tagged_playlists:
        return []
    tagged_songs = [song for playlist in tqdm(tagged_playlists, desc='Extracting tagged songs') for song in parse_str_list(playlist['songs'])]
    if not tagged_songs:
        return []
    tagged_song_counter = Counter(tagged_songs)
    most_common_songs = [song for song, _ in tagged_song_counter.most_common(1000)]
    converted_songs = melon_to_spotify(most_common_songs)
    popular_spotify_ids = converted_songs[:half_num_songs]
    remaining_needed = num_songs - len(popular_spotify_ids)
    random_songs = random.sample(tagged_songs, remaining_needed)
    
    target_song_indices = [song_index.get(song) for song in random_songs if song_index.get(song) is not None]
    if target_song_indices:
        knn_recommendations = apply_knn_songs(feature_matrix, target_song_indices, n_neighbors=5)
        knn_recommended_songs = [list(song_index.keys())[i] for i in knn_recommendations if i in song_index.values()]
    else:
        knn_recommended_songs = []
    # NaN 제거
    knn_recommended_songs = [song for song in knn_recommended_songs if song is not None]
    # KNN 추천 곡을 스포티파이 ID로 변환
    knn_spotify_ids = melon_to_spotify(knn_recommended_songs)
    # 최종 추천 리스트 생성
    final_recommendations = popular_spotify_ids + knn_spotify_ids
    # 추천곡 수가 부족할 경우 랜덤으로 채움
    if len(final_recommendations) < num_songs:
        additional_needed = num_songs - len(final_recommendations)
        additional_songs = random.sample(tagged_songs, additional_needed)
        additional_spotify_ids = melon_to_spotify(additional_songs)
        final_recommendations += additional_spotify_ids[:additional_needed]
    # 정확한 수의 곡을 보장하기 위해 자르기
    final_recommendations = final_recommendations[:num_songs]
    # 최종 추천 곡의 순서 랜덤 섞기
    random.shuffle(final_recommendations)

    return final_recommendations