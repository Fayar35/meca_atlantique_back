#!/usr/bin/python
# -*- coding: utf-8 -*-
import asyncio
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

def test(ip):
    # Création de l'instance LSV2
    client = pyLSV2.LSV2(ip, timeout=2, port=PORT_CNC, safe_mode=False)
    client.connect()
    print(client.versions)

    # for i in range(50):
    #     payload = bytearray()
    #     payload.extend(struct.pack('!H', i))
    #     ret = client._send_recive(pyLSV2.CMD.R_RI, payload, pyLSV2.RSP.S_RI)
    #     print(i, " : ", ret)
    # ret = client.read_data_path('/PLC/memory/K/1')

    # mdi_path = "TNC:/$MDI.H"
    # tool_t_path = "TNC:/TOOL.T"
    # with tempfile.TemporaryDirectory(suffix=None, prefix="pyLSV2_") as tmp_dir_name:
    #     local_mdi_path = Path("tmp_file")
    #     client.recive_file(local_path=str(local_mdi_path), remote_path="PLC:/LANGUAGE/ERR_TAB.PET", binary_mode=False, override_file=True)
            
    # client.change_directory("TNC:/")
    # dir = client.directory_info()
    # print(dir.path)
    # print("===========================")
    # files = client.directory_content()
    # for f in files:
    #     if not f._is_directory:
    #         print(f.name)

    # ret = client.read_plc_memory(264, pyLSV2.const.MemoryType.WORD, 1)
    ret = client.program_status()
    
    # payload = bytearray()
    # payload.extend(struct.pack('!H', 264))
    # ret = client._send_recive(pyLSV2.CMD.R_MB, payload, pyLSV2.RSP.S_MB)

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
    parser.add_argument("argument1", type=str, help="premier argument")

    args = parser.parse_args()

    if args.fonction == "get_status":
        print(get_status(args.argument1))
    elif args.fonction == "get_prg_name":
        print(get_prg_name(args.argument1))
    elif args.fonction == "test":
        retour = test(args.argument1)
        print(retour)
    elif args.fonction == "test2":
        print(test2(args.argument1))