from PySimpleAutomata import automata_IO

dfa_example = automata_IO.dfa_json_importer('dfa.json')

automata_IO.dfa_to_dot(dfa_example, 'output-name', './')
