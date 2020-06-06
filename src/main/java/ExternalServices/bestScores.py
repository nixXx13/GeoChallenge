import json
import boto3
import hashlib

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('best_score')
LEADERBOARD_SIZE = 10

def lambda_handler(event, context):
     
    # fill_dummy_db()
    game_scores = event['scores']
    user_list, score_list = list(game_scores.keys()), list(game_scores.values())
    score_list = [ int(float(s)*100) for s in score_list ]
    
    # current best scores
    top_user,top_score = get_all_items()
    
    to_be_saved_user = []
    to_be_saved_score = []
    
    # placing new scores in place
    for user,score in zip(user_list,score_list):
        for k in range(LEADERBOARD_SIZE):
            if score > top_score[k]:
                top_score.insert(k,score)
                top_user.insert(k,user)
                
                # saving new item, deleting last item
                save_item(user,score)
                delete_item( top_user[-1], top_score[-1] )
                top_user, top_score = top_user[:LEADERBOARD_SIZE], top_score[:LEADERBOARD_SIZE] 
                
                break
    
    top_score = [ float(s/100) for s in top_score ]
    return {
        'statusCode': 200,
        'scores' : json.dumps([top_user, top_score])
    }

def delete_item(user, score, table = None):
    if not table:
        dynamodb = boto3.resource('dynamodb')
        table = dynamodb.Table('best_score')
        
    try:
        response = table.delete_item(
            Key={
                'userId': str(txt_to_hash_int( "{}:{}".format(user,score) )),
            }
        )
    except ClientError as e:
        if e.response['Error']['Code'] == "ConditionalCheckFailedException":
            print(e.response['Error']['Message'])
        else:
            raise
    else:
        return response

def save_item(user, score):
    save_items([user], [score])

def save_items( user_list, score_list, table = None):
    if not table:
        dynamodb = boto3.resource('dynamodb')
        table = dynamodb.Table('best_score')
        
    for user,score in zip(user_list,score_list):
        response = table.put_item(
        Item={
            'userId': str(txt_to_hash_int( "{}:{}".format(user,score) )),
            'name': user,
            'score': score
        }
    )

def get_all_items():
    
    def take_second(elem):
        return elem[1]
        
    items = get_all_items_db()
    
    user_scores = [ (entry["name"],entry["score"]) for entry in items ]
    
    #sorting by score
    user_scores_sorted = sorted(user_scores,reverse=True, key=take_second)
    
    top_user = [ entry[0] for entry in user_scores_sorted ]
    top_score = [ entry[1] for entry in user_scores_sorted ]
    
    return top_user,top_score
    

def get_all_items_db(table = None):
    if not table:
        dynamodb = boto3.resource('dynamodb')
        table = dynamodb.Table('best_score')
    response = table.scan()
    return response.get('Items', [])
    
        
def txt_to_hash_int(txt, int_len = 10):
    a = hashlib.md5(txt.encode('utf-8'))
    b = a.hexdigest()
    as_int = int(b, 16)
    return (str(as_int)[:int_len])
    
def fill_dummy_db():
    users = [ "Mci" , "Ars" , "Lob", "Ya", "Ba", "Da", "Ba", "Do", "MicMic", "Mrs"]
    scores = [ i*100 for i in range(len(users)) ]
    save_items( users, scores )