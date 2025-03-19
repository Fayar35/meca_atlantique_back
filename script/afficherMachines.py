import requests

URL = "http://localhost:8080"

def afficher_machines():
    machines_response = requests.get(f"{URL}/getAllMachine")
    machines_json = machines_response.json()

    print("   |          nom          |        ip        |  port  ")
    print("---+-----------------------+------------------+--------")
    for i, machine in enumerate(machines_json):
        print(f"{format_to_n(str(i), 3)}| {format_to_n(machine["name"], 21)} | {format_to_n(machine["ip"], 16)} | {format_to_n(str(machine["port"]), 8)}")

    return machines_json

def format_to_n(str, n):
    if (len(str)>n):
        return str[:(n-3)].concat("...")
    else:
        return str.ljust(n)

if __name__ == '__main__':
    afficher_machines()