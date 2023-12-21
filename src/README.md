# Design Patterns 
## The MVC Architecture
### The Model 
- Hybrid of Piece-Centric / Square-centric Board Representation
- Performance Tradeoffs
  - Pros 
    - Readable and straightforward design 
      - Fast time to calculate checks via **color attack maps**  
        - O(1) hashset lookup of the opposite color's target squares to see if our king square is 
        contained in the set (indicating a check) 
  - Cons 
    - Some frequently used functions are relatively slow  
      - Inefficient to generate move subsets 
        - O(n) time to filter moves for moves flags like captures, castles, pawn promotions, etc. where 
      n is the number of moves generated. 

- Move generation
  - Staged move geneartion 
    - Pseudo-legal move generation 
    - Filtering pseudo-legal into legal 
  - Copy-make generation 
    - Copy make geneartion is selected over make / unmake 
      - To see if a pseudo-legal move is legal, the move is made and then 
      - Reduced complicated code / surface for bugs 
      - Simple design
      - I encountered some difficulties with infinite recursion / stack over flow 
        - When I tried to make a pseudo legal move, I call my model's makeMove()...
          - makeMove() calls my canMove() function to see if the move is legal
          - canMove() creates a copy and then calls makeMove() on the copy
          - the copy's makeMove() calls canMove() on the copy, which creates a new copy
          - The new copy calls makeMove() on itself, which calls canMove() on the new copy...
          - This continues until the stack overflow
### The Controller 

### The View

### Component Communication 

# The Engine
## Search  

### Minimax

### Alpha-beta pruning 

### Transposition Table 
- Try to keep this persistent across moves / searches 
- 
### Move ordering

### Killer Move Heuristic 

### Principle Variation (PV) Nodes 

### Iterative Deepening Search
- Search at a depth of one, then if search hasn't been cancelled yet, search 2 moves ahead, etc. 
  - Partial Search Persistence 
    - Have the move from the previous iteration ready to play
    - Begin each new search by looking at the best move from the previous iteration 
    - This ensures that results from partial searches do not go to waste
  
### Quiescence Search
- Keep searching until we reach a quiet position - one with no captures 
- 
### Search extension 
#### Check Extension
- If a move puts the enemy player in check, extend the search by a depth of one 
- Set extension limit to 16 
- This is because checks are more likely to be good moves
- It is inefficient to look equally deeply into all moves 
#### Pawn Promotion Extension   
- If a pawn is about to promote, extend the search to see if it successful 
### Different search tree node types

## Evaluation 

### Mop-up score 
- Encourage our king to help us checkmate in end game by favoring moves
that bring our king into the fight and closer to the enemy king 
### End-game weight manipulation

### Square Score Tables
- Favor corners and back rank to keep king safe 
- Favor pawns to move up the board towards promotion 

### Isolated passed pawns 
- Use a bitbaord w/ a passed pawn mask 
- Provide a bonus to our engine for pushing passed / isolated pawns

### Material Weights

# Testing
## Engine version improvements 


# Making moves 
- model.makeMove() --calls-> model.canMove() to verify move validity --> creates a copy model 
makeMove() is called on the copy model --> copy model calls canMove() on itself --> creates a new copy model
- This continues until the stack overflows


