import urllib
import urllib2

url = 'https://www.google.com/speech-api/v1/recognize?xjerr=1&client=chromium&lang=en-US'
audio_data = open('audio.flac', 'rb')
values = {'Content_Type' : 'audio/x-flac; rate=16000',
          'Content' : audio_data}
data = urllib.urlencode(values)
req = urllib2.Request(url, data)
response = urllib2.urlopen(req)
result = response.read()
print result
