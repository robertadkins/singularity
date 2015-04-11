import urllib2
import pyglet
import os

BASE_URL = 'http://tts-api.com/tts.mp3?q='
AUDIO_FILENAME = 'audio.mp3'

pyglet.resource.path = [os.getcwd()]
pyglet.resource.reindex()

''' Request audio from text. Save to file '''
def get_audio(text):
    formatted_text = urllib2.quote(text)

    audio_url = urllib2.urlopen(BASE_URL + formatted_text).geturl()
    audio_data = urllib2.urlopen(audio_url).read()

    audio_file = open(AUDIO_FILENAME, 'w')
    audio_file.write(audio_data)
    audio_file.flush()
    audio_file.close()

''' Play audio that was already requested and saved '''
def play_audio():
    audio = pyglet.resource.media(AUDIO_FILENAME)
    audio.play()
    pyglet.clock.schedule_once(exiter, audio.duration)
    pyglet.app.run()

''' Stops pyglet from hanging after sound has played '''
def exiter(dt):
    pyglet.app.exit()

get_audio(raw_input())
play_audio()



