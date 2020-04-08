from flask import Flask, request
import random
import pandas as pd

app = Flask(__name__)


@app.route('/data/geo/', methods=['GET'])
def get_tasks():
    g = GeoGen()
    return g.generate_questions(5)


@app.route('/data/math/', methods=['GET'])
def get_math_questions():
    m = MathGen()
    return m.generate_questions(5)


class MathFunction:

    def __init__(self, text, func):
        self.func = func
        self._text = text

    def __call__(self, *args, **kwargs):
        return self.func(args[0], args[1], args[2])

    @property
    def text(self):
        return self._text


class MathGen:

    def __init__(self):
        self.math_function_list = [
            MathFunction("{} x {} + {} = ?", lambda a, b, c: a*b+c),
            MathFunction("{} x {} - {} = ?", lambda a, b, c: a*b-c),
            MathFunction("{} + {} + {} = ?", lambda a, b, c: a+b+c),
            MathFunction("{} - {} + {} = ?", lambda a, b, c: a-b+c),
        ]

    def get_function(self):
        random.shuffle(self.math_function_list)
        return self.math_function_list[0]

    def generate_questions(self, q_number=5):

        questions = []

        for i in range(q_number):
            a = random.randint(1,11)
            b = random.randint(1,11)
            c = random.randint(1,11)
            func = self.get_function()
            answers = set()
            right_answer = func(a, b, c)
            answers.add(right_answer)
            while len(answers) < 4:
                other_answer = self._generate_other_answer(a, b, c, func)
                answers.add(other_answer)

            answers.remove(right_answer)
            formatted_question = to_question_format(func.text.format(a, b, c), right_answer, list(answers))
            questions.append(formatted_question)

        return to_question_list_format(questions)

    @staticmethod
    def _generate_other_answer(a, b, c, func):
        signs = [1,-1]

        random.shuffle(signs)
        a1 = random.randint(1, 4)*signs[0]
        random.shuffle(signs)
        b1 = random.randint(1, 4)*signs[0]
        random.shuffle(signs)
        c1 = random.randint(1, 4)*signs[0]
        return func(a+a1,b+b1,c+c1)


class GeoGen:

    def __init__(self):
        self.countries = pd.read_csv("countriesData_med.csv")

        self.labels = list(self.countries.columns.values)
        self.labels.remove("ID")
        self.labels.remove("NAME")
        self.labels.remove("GNI (U.S.$ )")
        self.labels.remove("MONETARY UNIT")

        self.qDict = {"CAPITAL": "Which of the following is the capital of {}",
                 "POPULATION": "Which of the following has the greatest population?",
                 "TOTAL AREA (SQ KM)": "Which of the following has the greatest area?",
                 "URBAN-RURAL POPULATION": "Which of the following has the greatest urban population?"
                 }

    def generate_questions(self, questions_number):

        # CAPITAL,POPULATION,TOTAL AREA (SQ KM),URBAN-RURAL POPULATION
        answers_number = 4

        qs = []

        for i in range(0, questions_number):

            indexes = random.sample(range(1, self.countries.shape[0]), answers_number)
            random.shuffle(self.labels)

            label = self.labels[0]
            if label != "CAPITAL":
                df = self.countries[self.countries["ID"].isin(indexes)]
                df = df.sort_values(label, ascending=0)
                answer = df['NAME'].iloc[0]
                other_answer1 = df['NAME'].iloc[1]
                other_answer2 = df['NAME'].iloc[2]
                other_answer3 = df['NAME'].iloc[3]
                formatted_question = to_question_format(self.qDict[label], answer, [other_answer1, other_answer2,
                                                                                    other_answer3])
                qs.append(formatted_question)
                # qs.append("{}:{},{},{},{}".format(self.qDict[label], pAnswer1, pAnswer2, pAnswer3, pAnswer4))

            else:
                df = self.countries[self.countries["ID"].isin(indexes)]
                q = df['NAME'].iloc[0]
                answer = df[label].iloc[0]
                other_answer1 = df[label].iloc[1]
                other_answer2 = df[label].iloc[2]
                other_answer3 = df[label].iloc[3]
                formatted_question = to_question_format(self.qDict[label].format(q), answer, [other_answer1, other_answer2,
                                                                                    other_answer3])
                qs.append(formatted_question)
                # qs.append("{}:{},{},{},{}".format(self.qDict[label].format(q), pAnswer1, pAnswer2, pAnswer3, pAnswer4))

        # return ";".join(qs)
        return to_question_list_format(qs)


def to_question_format(question, answer, other_answers):
    return "{}:{},{},{},{}".format(question, answer, other_answers[0], other_answers[1], other_answers[2])


def to_question_list_format(question_list):
    return ";".join(question_list)


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=500)
