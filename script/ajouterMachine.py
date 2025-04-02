import requests
import re

DEFAULT_PORT_FANUC = "8193"
DEFAULT_PORT_HEIDENHAIN = "19000"
DEFAULT_PORT_HURCO = "5000"
URL = "http://localhost:8080"

def ajouter_machine():
    # 1.fanuc 2.heidenhain
    print("Entrez le chiffre correspondant à la marque de la machine : 1.Fanuc 2.Heidenhain 3.Hurco")
    marque = input()
    if (marque == "q"): return
    while (marque != "1" and marque != "2" and marque != "3"):
        print("Entrez un chiffre entre 1 et 3")
        marque = input()
        if (marque == "q"): return

    # ip
    print("Sélectionnez l'ip de la machine à ajouter à la base de données :")
    ip = input()
    if (ip == "q"): return
    while (not bool(re.compile(r"^([0-9]{1,3}\.){3}([0-9]{1,3})$").match(ip))):
        print("ip sélectionnée mauvaise : ", ip)
        ip = input()
        if (ip == "q"): return

    # port
    default_port = DEFAULT_PORT_FANUC if marque == "1" else (DEFAULT_PORT_HEIDENHAIN if marque == "2" else DEFAULT_PORT_HURCO)
    print("Sélectionnez le port de la machine : touche entrée pour la valeur par défaut (", default_port, ")")
    port = input()
    if (port == "q"): return
    if (port == ""):
        port = default_port
    while (not bool(re.compile(r"^[0-9]{4,5}$").match(port))):
        print("port sélectionné mauvais : ", port)
        port = input()
        if (port == "q"): return
        if (port == ""):
            port = default_port

    # nom
    print("Sélectionnez le nom de la machine :")
    nom = input()

    table = "fanuc" if marque == "1" else ("heidenhain" if marque == "2" else "hurco")

    # résumé
    print(f"Inserer {nom} {ip}:{port} dans la table {table} ? [o/n]")
    validation = input()
    while (validation.lower() != "o"):
        print(f"Inserer {nom} {ip}:{port} dans la table {table} ? [o/n]")
        validation = input()
        if (validation.lower() == "n"): return

    response = requests.post(f"{URL}/{table}/createMachine?ip={ip}&name={nom}&port={port}")
    if (response.status_code == 200):
        print("Insertion de la machine dans la base de données réussie")
    else:
        print("Insertion de la machine dans la base de données échouée ", response.content)


ajouter_machine()