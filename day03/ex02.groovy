def fileContentAsArray = []
def file = new File('puzzle.txt').eachLine { fileContentAsArray.add(it.trim()) }
def rows = fileContentAsArray[0].size()
def columns = fileContentAsArray.size()
def fileContentAsArray_T = fileContentAsArray.collect{it -> it.collect{ itt -> Integer.parseInt(itt)}}.transpose()
def oxygen = fileContentAsArray
def oxygen_T = fileContentAsArray_T
def i = 0

//most common oxy
while(oxygen.size() > 1) {
    def ones = oxygen_T[i].count(1)
    def zeros =  oxygen_T[i].count(0)
    //println("#1= ${ones}, #0=${zeros}\noxygen= ${oxygen}")//transposed= ${oxygen_T}")
    if(ones >= zeros) {
        //print("remove=${oxygen.findAll{it.charAt(i) == '0'}}\n")
        oxygen -= oxygen.findAll{it.charAt(i) == '0'}
    } else {
        //print("remove=${oxygen.findAll{it.charAt(i) == '1'}}\n")
        oxygen -= oxygen.findAll{it.charAt(i) == '1'}
    }
    oxygen_T = oxygen.collect{it -> it.collect{ itt -> Integer.parseInt(itt)}}.transpose()
    i++
}
def oxygenRate= Integer.parseInt(oxygen[0], 2);
println("oxy rate= ${oxygenRate}")

def carbon = fileContentAsArray
def carbon_T = fileContentAsArray_T
i = 0
while(carbon.size() > 1) {
    def ones = carbon_T[i].count(1)
    def zeros =  carbon_T[i].count(0)
    if(ones >= zeros) {
        carbon -= carbon.findAll{it.charAt(i) == '1'}
    } else {
        carbon -= carbon.findAll{it.charAt(i) == '0'}
    }
    carbon_T = carbon.collect{it -> it.collect{ itt -> Integer.parseInt(itt)}}.transpose()
    i++
}

def carbonRate = Integer.parseInt(carbon[0], 2);
println("C02 scrubbing rate= ${carbonRate}")
println("life support rate= ${oxygenRate * carbonRate}")
return 0
