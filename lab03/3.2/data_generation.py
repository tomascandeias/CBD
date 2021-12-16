from random import randrange

def insert_user(username, nome, email, data_registo):
    return f"INSERT INTO utilizadores(username, nome, email, data_registo) VALUES(\'{username}\', \'{nome}\', \'{email}\', \'{data_registo}\')"

def insert_video(video_id, autor, titulo, descricao, tags, data_upload):
    return f"INSERT INTO video(video_id, autor, titulo, descricao, tags, data_upload) VALUES({video_id}, \'{autor}\', \'{titulo}\', \'{descricao}\', \'{tags}\', \'{data_upload}\');\n"

def insert_comentario(comentario_id, video_id, data, autor, comentario):
    return f"INSERT INTO comentario(comentario_id, video_id, data, autor, comentario) VALUES({comentario_id}, {video_id}, \'{data}\', \'{autor}\', \'{comentario}\');\n"

def insert_followers(video_id, username):
    return f"INSERT INTO followers(video_id, username) VALUES({video_id}, \'{username}\');\n"

def insert_eventos(video_id, username, tipo, data, momento):
    return f"INSERT INTO eventos(video_id, username, tipo, data, momento) VALUES({video_id}, \'{username}\', \'{tipo}\', \'{data}\', {momento});\n"

def insert_ratings(rating_id, video_id, rating):
    return f"INSERT INTO ratings(rating_id, video_id, rating) VALUES({rating_id}, {video_id}, {rating});\n"



with open("3.2/data.txt", "a") as f:
    autores = ["tomas123", "catarina98", "candeiaspt", "kapa112", "andreia_silva", "pedropt", "andre0w"]
    tags = ["comedia", "drama", "ação", "terror", "gameplay", "vlog", "politica"]
    tipos = ["play", "pause", "stop"]
    
    # videos 10
    for i in range(10):
        f.write(insert_video(i, autores[randrange(len(autores))], f"video{i}", "descricao", [tags[randrange(len(tags))], tags[randrange(len(tags))]], f"2021-12-1T12:{i}"))
    
    f.write("\n")
    # comentarios 20
    for i in range(20):
        f.write(insert_comentario(i, i, f"2021-12-1T12:{i}", autores[randrange(len(autores))], "comentario"))
    
    f.write("\n")
    # followers 20
    for i in range(10):
        f.write(insert_followers(i, autores[randrange(len(autores))]))
        f.write(insert_followers(i, autores[randrange(len(autores))]))
    
    f.write("\n")
    # eventos 10
    for i in range(10):
        f.write(insert_eventos(i, autores[randrange(len(autores))], tipos[randrange(len(tipos))], f"2021-12-1T12:{i}", randrange(1000)))

    f.write("\n")
    # rating 10
    for i in range(10):
        f.write(insert_ratings(i, i, randrange(6)))