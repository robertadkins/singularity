import urllib2
import pyglet
import os

BASE_URL = 'http://tts-api.com/tts.mp3?q='
AUDIO_FILENAME = 'audio.mp3'

pyglet.resource.path = [os.getcwd()]
pyglet.resource.reindex()

text = raw_input()
formatted_text = urllib2.quote(text)

audio_url = urllib2.urlopen(BASE_URL + formatted_text).geturl()
audio_data = urllib2.urlopen(audio_url).read()

audio_file = open(AUDIO_FILENAME, 'w')
audio_file.write(audio_data)
audio_file.close()

audio = pyglet.resource.media(AUDIO_FILENAME)
audio.play()
