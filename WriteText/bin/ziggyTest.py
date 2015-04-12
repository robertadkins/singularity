import urllib
import urllib2
import datetime
import json
import hmac
import hashlib
import base64

def create_ask_ziggy_api_url(naturalLanguageString, apiKey, secretKey, endUserId, apiVersion):
    # Generating timestamp
    timestamp = datetime.datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S")
    timestamp += 'Z'
    # URL encode parameters
    naturalLanguageString = urllib.urlencode({'input' : naturalLanguageString})
    timestamp = urllib.urlencode({'timestamp' : timestamp})
    # Generating the base string for HMAC SHA-256 encryption
    base_string = "%s&%s&apikey=%s&userid=%s&apiversion=%s&secretkey=%s" % (naturalLanguageString, timestamp, apiKey, endUserId, apiVersion, secretKey)
    base_string = base_string.replace(' ', '')
    base_string = base_string.replace('%20', '')
    base_string = base_string.replace('+', '')
    signature = base64.b64encode(hmac.new(secretKey, msg=base_string, digestmod=hashlib.sha256).digest())
    signature = urllib.urlencode({'signature' : signature})
    # Generate url string with naturalLanguageString, timestamp, apiKey, endUserId, and signature
    url = "https://www.ask-ziggy.net/NLP/api/parse"
    url = "%s?%s&%s&apikey=%s&userid=%s&%s&apiversion=%s" % (url, naturalLanguageString, timestamp, apiKey, endUserId, signature, apiVersion)
    return url

def call(string):
    # Setup
    naturalLanguageString = string
    apiKey = 'ea414cf0'
    secretKey = 'f0eb0ffc5147f3a550df8746310f5f42'
    endUserId = ''
    apiVersion = '1'
    # Create Ask Ziggy API URL
    url = create_ask_ziggy_api_url(naturalLanguageString, apiKey, secretKey, endUserId, apiVersion)
    # Generate JSON results
    data = json.load(urllib2.urlopen(url))
    print(data)

while(True):
    test = raw_input()
    call(test)
