import re
import requests
from afficherMachines import afficher_machines

URL = "http://localhost:8080"

def modifier_machine():
    machines = afficher_machines()
    print()
    print("Sélectionnez le numéro correspondant à la machine à modifier ('q' pour quitter le programme) :")
    id = input()
    if (id == "q"): return

    while(id not in [str(i) for i in range(len(machines))]):
        print(f"Entrez un numéro entre 0 et {len(machines)-1}")
        id = input()
        if (id == "q"): return

    machine = machines[int(id)]

    default_port = machine["port"]
    print("Sélectionnez le nouveau port de la machine : touche entrée ne pas modifier (", default_port, ")")
    
    port = input()
    if (port == "q"): return
    if (port == ""):
        port = default_port
    else:
        while (not bool(re.compile(r"^[0-9]{4,5}$").match(port))):
            print("port sélectionné mauvais : ", port)
            port = input()
            if (port == "q"): return
            if (port == ""):
                port = default_port

    default_name = machine["name"]
    print("Sélectionnez le nouveau nom de la machine : touche entrée ne pas modifier (", default_name, ")")
    
    name = input()
    if (name == "q"): return
    if (name == ""):
        name = default_name

    newMachine = {
        "ip": machine["ip"],
        "port": port,
        "name": name,
    }

    response = requests.put(f"{URL}/updateMachine", json=newMachine)

    if (response.status_code == 200):
        print("Modification de la machine réussie")
    else:
        print("Modification de la machine échouée ", response.content)


if __name__ == '__main__':
    modifier_machine()