import requests
from afficherMachines import afficher_machines

URL = "http://localhost:8080"

def supprimer_machine():
    while(True):
        machines = afficher_machines()
        print()
        print("Sélectionnez le numéro correspondant à la machine à supprimer de la base de données ('q' pour quitter le programme) :")
        id = input()
        if (id == "q"): return

        while(id not in [str(i) for i in range(len(machines))]):
            print(f"Entrez un numéro entre 0 et {len(machines)-1}")
            id = input()
            if (id == "q"): return

        response = requests.delete(f"{URL}/deleteMachine?ip={machines[int(id)]["ip"]}")
        if (response.status_code == 200):
            print("Suppression de la machine de la base de données réussie")
        else:
            print("Suppression de la machine de la base de données échouée ", response.content)

if __name__ == '__main__':
    supprimer_machine()