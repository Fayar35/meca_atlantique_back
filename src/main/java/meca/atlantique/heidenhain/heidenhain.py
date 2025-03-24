#!/usr/bin/python
# -*- coding: utf-8 -*-
import struct
import pyLSV2
import argparse
import sys, os
import serial

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
    # with HiddenPrints():
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
    serial_connection = serial.Serial(
        port="COM3",        # Port série
        baudrate=9600,      # Vitesse de transmission (à adapter selon votre machine)
        bytesize=8,         # Taille des données
        parity='N',         # Parité (None)
        stopbits=1,         # Bits de stop
        timeout=1           # Timeout en secondes
    )

    payload = bytearray([
        0x00, 0x00, 0x00,   # Trois octets '0'
        0x02,               # Octet 0x2
        0x52, 0x5F, 0x52, 0x49,  # ASCII pour "R_RI"
        0x00,               # Octet 0x00
        0x18                # Octet 0x18
    ])
    print(serial_connection.portstr)

    serial_connection.write(payload)
    response = serial_connection.readline()
    print(response)
    # Création de l'instance LSV2
    # client = pyLSV2.LSV2(serial_connection)
    # client.connect()
    # # client.login(pyLSV2.const.Login.PLCDEBUG)
    # # payload = bytearray()
    # # payload.extend(struct.pack('!H', 21))
    # # ret = client._send_recive(pyLSV2.CMD.R_RI, payload, pyLSV2.RSP.S_RI)
    # ret = client.versions.control
    # # client.disconnect()
    # return ret

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