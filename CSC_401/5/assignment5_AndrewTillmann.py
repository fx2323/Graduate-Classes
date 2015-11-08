def hexASCII():
    'prints hexadecimal representation of lowercase English alphabet'

    letters='abcdefghijklmnopqrstuvwxyz'

    for letter in letters:
        print('{}:{:x}'.format(letter,ord(letter)))
    
    




def simul(n):
    'simulates n rounds of rock, paper, sissors prints the winner'

    #import needed module
    import random

    #set variables needed to simulate the game
    dictionary={'RS':-1,'SP':-1,'PR':-1,'SR':1,'PS':1,'RP':1,'RR':0,'SS':0,'PP':0}
    seq=['R','P','S']
    count=0

    #simulate the game
    for i in range(n):
        player1=random.choice(seq)
        player2=random.choice(seq)
        key=player1+player2
        
        count += dictionary[key]

    #print the winner
    if count<0:
        print('Player 1')
    elif count>0:
        print('Player 2')
    else:
        print('Tie')


def craps():
    'simulates one game of craps'

    #import need module
    import random

    #set the variables
    outcome={'2':0,'3':0,'12':0,'7':1,'11':1}

    #plays the game
    n=0
    while n==0:
        die1=random.randrange(1,6)
        die2=random.randrange(1,6)
        roll=str(die1+die2)
        if roll in outcome:
            return outcome[roll]

        #if len(outcome)==5 then must be first roll
        elif len(outcome)==5:
            outcome={roll:1,'7':0} #new rules initial roll value wins, and 7 loses

            

def testCraps(n):
    'simulates craps() n number of times'

    #set the variables
    cycle=0
    runningSum=0

    #cycles through code n number of times
    while n>cycle:
        runningSum += craps()
        cycle += 1

    return runningSum/n        
