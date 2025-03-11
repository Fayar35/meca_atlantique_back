#!/usr/bin/python
# -*- coding: utf-8 -*-
import struct
import pyLSV2
import argparse
import sys, os

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
        client = pyLSV2.LSV2(ip, timeout=2, port=PORT_CNC)
        client.connect()
        ret = client.program_status()-1
        client.disconnect()
        return ret
    
def get_prg_name(ip):
    with HiddenPrints():
        # Création de l'instance LSV2
        client = pyLSV2.LSV2(ip, timeout=2, port=PORT_CNC)
        client.connect()
        client.login(pyLSV2.const.Login.INSPECT)
        payload = bytearray()
        payload.extend(struct.pack('!H', CURRENT_PRG))
        ret = client._send_recive(pyLSV2.CMD.R_RI, payload, pyLSV2.RSP.S_RI)
        client.disconnect()
        return ret

def test(ip):
    client = pyLSV2.LSV2(ip, timeout=2, port=PORT_CNC)
    client.connect()
    client.login(pyLSV2.Login.PLCDEBUG)
    ret = client.execution_state()-1
    ret = pyLSV2.ExecState(ret).name
    client.disconnect()
    return ret

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Exécute une fonction spécifique.")
    parser.add_argument("fonction", type=str, help="Nom de la fonction à exécuter")
    parser.add_argument("argument1", type=str, help="premier argument")

    args = parser.parse_args()

    if args.fonction == "get_status":
        print(get_status(args.argument1))
    elif args.fonction == "get_prg_name":
        print(get_prg_name(args.argument1).decode())
    elif args.fonction == "test":
        retour = test(args.argument1)
        print(retour)