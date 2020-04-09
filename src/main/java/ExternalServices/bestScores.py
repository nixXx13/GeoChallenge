from flask import Flask, request

app = Flask(__name__)
GEO_FILE = "geoScores.txt"
MATH_FILE = "mathScores.txt"


@app.route('/data/geo/', methods=['GET'])
def get_geo_scores():
    return read_file(GEO_FILE)


@app.route('/data/math/', methods=['GET'])
def get_math_scores():
    return read_file(MATH_FILE)


@app.route('/data/set_geo/', methods=['POST'])
def update_geo_scores():
    data = request.form
    user_score_pair_string = data["body"]
    user_list, score_list = user_score_pair_string_to_lists(user_score_pair_string)
    new_top = calculate_new_top(GEO_FILE, user_list, score_list)
    write_file(GEO_FILE, new_top)
    return new_top


@app.route('/data/set_math/', methods=['POST'])
def update_math_scores():
    data = request.form
    user_score_pair_string = data["body"]
    user_list, score_list = user_score_pair_string_to_lists(user_score_pair_string)
    new_top = calculate_new_top(MATH_FILE, user_list, score_list)
    write_file(MATH_FILE, new_top)
    return new_top


def calculate_new_top(file, user_list, score_list):
    scores_line = read_file(file)

    top_user_list, top_score_list = user_score_pair_string_to_lists(scores_line)

    for i in range(len(user_list)):
        user = user_list[i]
        score = float(score_list[i])
        for k in range(len(top_user_list)):
            if score > top_score_list[k]:
                top_score_list.insert(k,score)
                top_user_list.insert(k,user)
                break

    top_score_list = top_score_list[:10]
    top_user_list = top_user_list[:10]

    new_top_list = []
    for i in range(len(top_user_list)):
        user = top_user_list[i]
        score = top_score_list[i]
        new_top_list.append("{}:{}".format(user,score))

    return ",".join(new_top_list)


def user_score_pair_string_to_lists(user_score_pair_string):
    user_score_pairs = user_score_pair_string.split(",")
    user_list = []
    score_list = []

    for pair in user_score_pairs:
        if pair != "":
            user,score = pair.split(":")
            user_list.append(user)
            score_list.append(float(score))

    return user_list, score_list


def write_file(file, content):
    f = open(file,'w')
    f.write(content)


def read_file(file):
    f = open(file)
    scores = f.read()

    f.close()
    return scores

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=600)
