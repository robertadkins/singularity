import json
import re
import random


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
	
	# 12AM
	if char_name == "EIGHT":
	    play = re.sub(r'\n([A-Z]+).*\. ', r'\n\1\t', play)        
            play = re.sub(r'\(.*\)', '', play)
        
        # Doll house
        if char_name == "Nora":
	    play = re.sub(r'\n\n([A-Z][a-z]+).*\. ', r'\n\1\t', play)        
            play = re.sub(r'\[.*\]', '', play)
#        print play

        # Big love
        if char_name == "LYDIA":
            play = re.sub(r'\n([A-Z]+)\n', r'\n\1\t', play)
            play = re.sub(r'\[.*\]', '', play)
            play = re.sub(r'\n\n', r'\n', play)
            play = re.sub(r'\n([A-Z]?[^A-Z][^\n]*)', r' \1', play)

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
    
    while next_word not in markov["_end"]:
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


linesM = get_lines("macbeth.txt", "MACBETH")
markovM = gen_markov(linesM.values())


linesG = get_lines("godot.txt", "ESTRAGON")
markovG = gen_markov(linesG.values())

linesT = get_lines("12AM.txt", "EIGHT")
markovT = gen_markov(linesT.values())

linesD = get_lines("dollhouse.txt", "Nora")
markovD = gen_markov(linesD.values())

linesB = get_lines("biglove.txt", "LYDIA")
markovB = gen_markov(linesB.values())

#t = generate(raw_input("> "), markovT, linesT)
#print t

#print linesB.values()

linesUltimate = linesM.copy()
linesUltimate.update(linesG)
linesUltimate.update(linesT)
linesUltimate.update(linesD)
linesUltimate.update(linesB)

markovUltimate = gen_markov(linesUltimate.values())

while True:
    s = raw_input("> ")
    #t =generate(t, markovM, linesM)
    #print t
    t = generate(s, markovUltimate, linesUltimate)
    print t
