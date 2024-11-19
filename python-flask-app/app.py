import os
import json
from flask import Flask, request, jsonify
from datetime import datetime
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser

app = Flask(__name__)

# 환경 변수에서 OpenAI API 키 가져오기
api_key = os.getenv("OPENAI_API_KEY")

class Model:
    def __init__(self, user_input):
        self.input = user_input

    def answer(self):
        now = datetime.now()
        output_parser = StrOutputParser()

        prompt1 = ChatPromptTemplate.from_messages([
            ('system', '''이 시스템은 일련의 답변을 제공합니다.
            유저의 최종 목표에 맞추어 단계적인 세부 목표를 설정합니다.
            세부 목표의 개수는 최소 6~12개가 되어야 합니다.
            예시.
            1. 유니티 다운로드 및 설치
            - 유니티 공식 웹사이트에서 유니티 허브를 다운로드하고 설치합니다.

            2. 유니티 인터페이스 이해
            - 유니티의 기본적인 인터페이스 및 기능에 익숙해지고 간단한 프로젝트를 생성해 봅니다.

            3. C# 프로그래밍 언어 학습
            - 유니티에서 스크립트 작성을 위해 C# 프로그래밍 언어를 학습합니다.

            4. 기초적인 게임 만들기
            - 유니티를 사용하여 간단한 2D 또는 3D 게임을 만들어 봅니다. 예를 들어, 움직이는 캐릭터, 충돌 감지, 점수 계산 등을 구현해 봅니다.

            5. 애니메이션 및 이펙트 추가
            - 유니티의 애니메이션 시스템을 활용하여 캐릭터 애니메이션 및 이펙트를 추가해 봅니다.

            6. 사운드 및 UI 구현
            - 게임에 사운드 이펙트를 추가하고 사용자 인터페이스(UI)를 디자인하여 게임의 완성도를 높입니다.

            7. 게임 최적화 및 배포
            - 게임을 최적화하여 성능을 향상시키고, 원하는 플랫폼에 게임을 배포해 보면서 유니티의 다양한 기능을 익힙니다.
            '''),
            ('user', '{user_input}')
        ])

        prompt2 = ChatPromptTemplate.from_messages([
            ('system', f"""주어진 일련의 목표들을 각각의 JSON 형식으로 출력합니다.
            JSON파일의 형식은 아래와 같습니다.
            필수적인 속성값은 seq, tobic, description, start_date, deadline, note 을 가진 JSON형식이어야 합니다.

            seq: topic의 고유한 식별자로 1부터 오름차순으로 정수 번호를 부여합니다.
            topic: 최종 목표를 위한 세부 목표,
            description: 과정 설명 및 목표에 대한 세부 설명,
            start_date: 현재 날짜를 기준으로 세부 목표의 시작 날짜(예시. 2000-00-00)
            deadline: 현재 날짜를 기준으로 세부 목표의 예상 데드라인(예시. 2000-00-00)
            note: 추후 사용자가 메모할 수 있도록 기능을 추가할 예정입니다. 답변 생성 시에는 null 값으로 생성합니다.

            현재 시간은 {now.date()}입니다.
            각각의 topic과 description에 대해 deadline은 현실적인 마감시간을 설정해야 합니다.
            난이도를 상,중,하로 나누고 난이도 상-한 달, 중-2주, 하-이틀 이상의 시간을 부여합니다.
            각각의 세부 목표는 순차적으로 이루어진다고 가정합니다.

            json 파일은 배열 형태로 리턴합니다.
            """),
            ('user', '{user_input}')
        ])

        llm = ChatOpenAI(
            model="gpt-3.5-turbo-0125",
            temperature=0.1,
            openai_api_key=api_key
        )

        # 두 프롬프트 체인 실행
        chain1 = prompt1 | llm | output_parser
        chain2 = {"user_input": chain1} | prompt2 | llm | output_parser

        user_input = self.input
        response = chain2.invoke({'user_input': f'"{user_input}"을(를) 공부하려면 어떻게 공부, 연습해야 할까?'})
        # 반환 데이터를 JSON 객체로 디코드
        decoded_response = json.loads(response)
        return decoded_response

@app.route('/createroadmap', methods=['GET'])
def generate_plan():
    topic = request.args.get("topic")
    model = Model(topic)
    result = model.answer()
    #return jsonify(result=result)
    # Flask에서 JSON 응답 생성 (pretty-printed JSON)
    response = jsonify(result)
    response.headers['Content-Type'] = 'application/json; charset=utf-8'
    return response

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
