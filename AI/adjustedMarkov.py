import json
import re

def adjustments(input_str, speeches):
    appearances = {}

    for word_dirty in input_str.split(" "):
        print word_dirty
        word = re.search(r'[A-Za-z\']*', word_dirty).group(0)

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
        play = re.sub(r'\n([A-Za-z]*)\n\n',r'\1\t', play)
        play = re.sub(r'(.*)\n    (.*)',r'\1 \2', play)

        character_lines = re.findall(re.compile('[A-Za-z]*\t[^\n\t]*\n%s\t[^\n\t]*' % character_name), play)

        prev_line_re = re.compile('    ([^\t\n]*)\n%s' % character_name)
        current_line_re = re.compile('%s\t    ([^\n\t]*)' % character_name)
        for line in character_lines:
            prev_line = re.search(prev_line_re, line).group(1)
            current_line = re.search(current_line_re, line).group(1)
            lines[prev_line] = current_line
        
    return lines

def read_markov(filename):
    markov = {}
    with open(filename) as markov_file:
        markov = json.load(markov_file)
    return markov

def generate(input_str):
    adj = adjustments(input_str, get_lines("macbeth.txt", "MACBETH"))
    markov = read_markov("markov.json")

    
adjustments("Macduff", get_lines("macbeth.txt", "MACBETH"))
