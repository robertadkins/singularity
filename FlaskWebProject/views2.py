import json
import re
import random

from datetime import datetime
from flask import render_template
from FlaskWebProject import app

@app.route('/')
@app.route('/home')
def home():
    """Renders the home page."""
    return render_template(
        'index.html',
        title='Home Page',
        year=datetime.now().year,
    )

#@app.route('/contact')
#def contact():
#    """Renders the contact page."""
#    return render_template(
#        'contact.html',
#        title='Contact',
#        year=datetime.now().year,
#        message='Your contact page.'
#    )

@app.route('/about')
def about():
    """#Renders the about page."""
 
    return render_template(
        'about.html',
        title='About',
        year=datetime.now().year,
        message='Your application description page.'
    )



@app.route('/text/<stt>')
def save_to_file(stt):
	linesM = get_lines("macbeth.txt", "MACBETH")
	markovM = gen_markov(linesM.values())

	t = generate(stt, markovM, linesM)
	print t

#if __name__ == '__main__':
#	app.run(host='10.122.1.106', port=33334)

def adjustments(input_str, speeches):
	simple_words = ["i", "a", "an", "and", "the", "then", "it"]

	appearances = {}

	for word_dirty in input_str.split(" "):
		word = re.search(r'[A-Za-z\']*', word_dirty).group(0)
		if word not in simple_words:
		
			speeches_with_word = dict([(key, value) for key, value in speeches.iteritems() if word in key or word in value])
			
			for key, speech in speeches_with_word.iteritems():
				for speech_word_dirty in speech.split(" "):
					speech_word = re.search(r'[A-Za-z\']*', speech_word_dirty).group(0).lower()

					if not speech_word in appearances:
						appearances[speech_word] = 1
					else:
						appearances[speech_word] += 1
	return appearances

def get_lines(filename, char_name):
	lines = {}
	with open(filename) as playFile:
		tolerance = 1
		character_name = char_name
		failed_message = "Forsooth!"
		
		play = playFile.read()
		# Macbeth
		if char_name == "MACBETH":
			play = re.sub(r'\n([A-Za-z]*)\n\n',r'\1\t', play)
			play = re.sub(r'(.*)\n    (.*)',r'\1 \2', play)
			play = re.sub(r'\[Aside\]', '', play)

		# Godot
		if char_name == "ESTRAGON":
			play = re.sub(r'\n([A-Z]+)\n    ', r'\n\1\t', play)
			play = re.sub(r'\((.*)\).?', r'', play)
			play = re.sub(r' \. \. \.', '...', play)
		
#        print play
		character_lines = re.findall(re.compile('[A-Za-z]*\t[^\n\t]*\n%s\t[^\n\t]*' % character_name), play)

		prev_line_re = re.compile('([^\t\n]*)\n%s' % character_name)
		current_line_re = re.compile('%s\t([^\n\t]*)' % character_name)
		for line in character_lines:
#            print line
			prev_line = re.search(prev_line_re, line).group(1)
			current_line = re.search(current_line_re, line).group(1)
			lines[prev_line] = current_line
		
	return lines

def add_words(line, markov):
	words = line.split(" ")

	for i, word in enumerate(words):
		if i==0:
			if word in markov["_start"]:
				markov["_start"][word] += 1
			else:
				markov["_start"][word] = 1
		if i==len(words)-1:
			if word in markov["_end"]:
				markov["_end"][word] += 1
			else:
				markov["_end"][word] = 1
		else:
			if word in markov:
				if words[i+1] in markov[word]:
					markov[word][words[i+1]] += 1
				else:
					markov[word][words[i+1]] = 1
			else:
				markov[word] = {}
				markov[word][words[i+1]] = 1

def gen_markov(lines):
	markov = {}

	markov["_start"] = {}
	markov["_end"] = {}

	for line in lines:
		add_words(line, markov)
		
	return markov

def generate(input_str, markov, lines):
	adj = adjustments(input_str, lines)

	next_word = "_start"
	output_str = ""
	
	while next_word[-1:] != "." and next_word[-1:] != "?" and next_word[-1:] != "!":
		max_rand = 0

		possibilities = markov[next_word].copy()
		for key in possibilities.keys():
			clean_key = re.search(r'[A-Za-z\']*', key).group(0).lower()
			if clean_key in adj:
				possibilities[key] += adj[clean_key]
			else:
				possibilities[key] /= 100.0
			max_rand += possibilities[key]

		rand_val = random.random()*max_rand
		
		for key, value in possibilities.iteritems():
			if rand_val < value:
				next_word = key
				output_str += next_word + " "
				break
			rand_val -= value
			
	return output_str
