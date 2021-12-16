import json
import random
    
def main():
    name = "bike"
    id_bike = "bk"
    model = ["active", "kids", "estrada", "montanha"]
    size = ["S", "M", "L"]
    material = ["aluminio", "titânio", "fibra de carbono"]
    
    with open("CBD_L205/data.json", "a") as f:
        for i in range(1, 82422):
            data = {}
            data["id_bike"] = id_bike + str(i)
            data["name"] = name + str(i)
            data["price"] = random.randint(100, 5000)
            
            data2 = {}
            data2["model"] = model[random.randint(0, len(model)-1)]
            data2["size"] = size[random.randint(0, len(size)-1)]
            data2["material"] = material[random.randint(0, len(material)-1)]
            
            data["specs"] = data2
            
            f.write(json.dumps(data))
        
    
    
    
    
        
        
    
    
    

if __name__ == '__main__':
    main()