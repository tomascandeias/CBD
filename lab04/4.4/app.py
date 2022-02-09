from neo4j import GraphDatabase

# MUST SEE: aux file with the nodes, relationships and queries developed to write the app.py

# https://neo4j.com/developer/python/#python-driver
# Based on the class from he no4j documentation
class Neo4JDatabase:

    def __init__(self, uri, user, password):
        self.driver = GraphDatabase.driver(uri, auth=(user, password))

    def close(self):
        self.driver.close()

    def query(self, query):
        with self.driver.session() as session:
            session.run(query)

    def query_with_result(self, query):
        lst = []
        with self.driver.session() as session:
            for node in session.run(query):
                lst.append(node)
        return set(lst)


if __name__ == "__main__":
    db = Neo4JDatabase("bolt://localhost:7687", "neo4j", "123")

    # Test connection
    try:
        db.driver.session().run("Match () Return 1 Limit 1")
        print('Connection established with success!\n')

    except Exception:
        print('Could not connect to the desire database')
        exit(1)


    print("Inserting...")
    print("INSERT Pokemons")
    db.query("""LOAD CSV WITH HEADERS
                FROM 'file:///pokemon.csv' as row
                MERGE (pokemon:Pokemon {name: row.Name})
                SET pokemon.number=row.Entry, pokemon.total=row.Total, pokemon.hp=row.HP, pokemon.attack=row.Attack, pokemon.defense=row.Defense, pokemon.spatk=row.Sp.Atk, pokemon.spdef=row.Sp.Def, pokemon.speed=row.Speed
                """)

    print("INSERT Types")
    db.query("""LOAD CSV WITH HEADERS
                FROM 'file:///pokemon.csv' as row
                WITH row WHERE row.Type1 IS NOT NULL
                MERGE (type:Type {type: row.Type1})
                """)

    db.query("""LOAD CSV WITH HEADERS
                FROM 'file:///pokemon.csv' as row
                WITH row WHERE row.Type2 IS NOT NULL
                MERGE (type:Type {type: row.Type2})
                """)

    print("INSERT Generations")
    db.query("""LOAD CSV WITH HEADERS
                FROM 'file:///pokemon.csv' as row
                MERGE (gen:Generation {number: row.Generation})
                """)

    print("INSERT Rel Type")
    db.query("""LOAD CSV WITH HEADERS
                FROM 'file:///pokemon.csv' as row
                MATCH (pokemon:Pokemon {name: row.Name}),(type:Type) CREATE (pokemon)-[:Type]->(type)
                """)

    print("INSERT Rel MEMBER_OF_GEN")
    db.query("""LOAD CSV WITH HEADERS
                FROM 'file:///pokemon.csv' as row
                MATCH (pokemon:Pokemon {name: row.Name}),(gen:Generation {number: row.Generation}) CREATE (pokemon)-[:MEMBER_OF_GEN]->(gen)
                """)

    print("All nodes and relationships have been inserted!\n")

    print("Executing 10 queries...")

    prompts = ["Get all Pokemon names",
               "Get all Pokemon types",
               "Get all Pokemons with more attack than defense",
               "Get the top 5 fire pokemons with the highest attack",
               "Get all first generation pokemons with defense over 85",
               "Get the average attack number for fighting pokemons",
               "What's the maximum distance between two pokemons",
               "Get all pokemons names which contain a 'w' in his name",
               "Get top 5 fastest pokemons",
               "Get all second generation water pokemons with overall above 400"]

    queries = ["match (p:Pokemon) return p.name;",
               "match (t:Type) return t.type;",
               "match (p:Pokemon) where toInteger(p.attack)-toInteger(p.defense)>0 return p.name;",
               "match (p:Pokemon)-[:Type]->(:Type {type: \"Fire\"}) return p.name order by p.attack desc limit 5;",
               "match (p:Pokemon)-[:MEMBER_OF_GEN]->(:Generation {number: \"1\"}) where toInteger(p.defense)>85 return p.name;",
               "match (p:Pokemon)-[:Type]->(:Type {type: \"Fighting\"}) return avg(toInteger(p.attack));",
               "match (p1:Pokemon), (p2:Pokemon) where p1<>p2 match path=shortestPath((p1)-[*]-(p2)) return max(length(path));",
               "match (p:Pokemon) where p.name contains \"w\" return p.name;",
               "match (p:Pokemon) return p.name order by p.speed desc limit 5;",
               "match (p:Pokemon)-[:MEMBER_OF_GEN]->(:Generation {number: \"2\"}) match (p)-[:Type]->(:Type {type: \"Water\"}) where toInteger(p.total)>400 return p.name;"]

    with open("CBD_L44_c_output.txt", "w") as f:
        count = 1
        for p, q in zip(prompts, queries):
            f.write(str(count) + ".\n")
            f.write("Question: " + p + "\n")
            f.write("Query: " + q + "\n\n")

            query_result = db.query_with_result(q)
            for res in query_result:
                f.write(str(res) + "\n")

            f.write("\n")
            count += 1

    print("See the output in the CBD_L44_c_output.txt")
    db.close()
