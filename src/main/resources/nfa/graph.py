from PySimpleAutomata import automata_IO

nfa_example = automata_IO.nfa_json_importer('nfa.json')

automata_IO.nfa_to_dot(nfa_example, 'nfa-diag', './')
