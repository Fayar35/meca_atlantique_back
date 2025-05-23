#!/usr/bin/python
# -*- coding: utf-8 -*-
import asyncio
import datetime
from pathlib import Path
import struct
import tempfile
import pyLSV2
import argparse
import sys, os
import serial
import time
import snap7
from snap7 import Client, Row, DB
import telnetlib3

PORT_CNC = 19000  # Port par défaut pour LSV/2
CURRENT_PRG = 24
CURRENT_STATUS = 26

class HiddenPrints:
    def __enter__(self):
        self._original_stdout = sys.stdout
        sys.stdout = open(os.devnull, 'w')
        sys.stderr = open(os.devnull, 'w')

    def __exit__(self, exc_type, exc_val, exc_tb):
        sys.stdout.close()
        sys.stdout = self._original_stdout
        sys.stderr = self._original_stdout

def get_status(ip):
    with HiddenPrints():
        # Création de l'instance LSV2
        client = pyLSV2.LSV2(ip, timeout=2, port=PORT_CNC, safe_mode=False)
        client.connect()
        ret = client.program_status()
        client.disconnect()
        return ret
    
def get_prg_name(ip):
    with HiddenPrints():
        # Création de l'instance LSV2
        client = pyLSV2.LSV2(ip, timeout=2, port=PORT_CNC, safe_mode=False)
        client.connect()
        ret = client.program_stack().main
        client.disconnect()
        return ret

def get_alarms(ip):
    with HiddenPrints():
        # Création de l'instance LSV2
        client = pyLSV2.LSV2(ip, timeout=2, port=PORT_CNC, safe_mode=False)
        client.connect()
        client.login(pyLSV2.Login.DNC)
        ret = client.get_error_messages()
        client.disconnect()
        return ret

def test2(ip):
    client = pyLSV2.LSV2(ip, timeout=2, port=9001, safe_mode=False)
    client.connect()
    ret = client.program_stack()
    client.disconnect()
    return ret

    # with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    #     s.connect((ip, 9001))
    #     s.sendall(b'\x05')  # LSV2 handshake start (ENQ)
    #     data = s.recv(1024)
    #     print("Réponse brute:", data)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Exécute une fonction spécifique.")
    parser.add_argument("fonction", type=str, help="Nom de la fonction à exécuter")
    parser.add_argument("ip", type=str, help="ip de la machine")

    args = parser.parse_args()

    if args.fonction == "get_status":
        print(get_status(args.ip))
    elif args.fonction == "get_prg_name":
        print(get_prg_name(args.ip))
    elif args.fonction == "get_alarms":
        retour = get_alarms(args.ip)
        for r in retour: 
            if r.e_class == 7 and r.e_group in [1,3,5,6,7]:
                if r.e_number != -2130705608: #numéro de "824 !Attention-Zone de collision Z!"
                    print("ALARM : " + r.e_text)
    elif args.fonction == "test2":
        print(test2(args.ip))