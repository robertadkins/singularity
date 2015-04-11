import requests

def call(command_history):
    payload = {
        "botid":"13602",
        
        }

    r = requests.post("http://www.cleverscript.com/bots/chat/?#chat", data = payload)
    print r.text.encode("utf8","ignore")

call(0)
