#!/usr/bin/python
# -*- coding: utf-8 -*-
import pyLSV2

# Configuration de l'adresse IP et du port de la CNC
IP_CNC = "192.168.0.122"
PORT_CNC = 19000  # Port par défaut pour LSV/2

def get_spindle():
    # Création de l'instance LSV2
    client = pyLSV2.LSV2(IP_CNC)
    client.connect()
    client.login(pyLSV2.const.Login.DNC)
    ret = client.spindle_tool_status()
    client.disconnect()
    if ret is pyLSV2.dat_cls.ToolInformation:
        return ret
    
print(get_spindle())