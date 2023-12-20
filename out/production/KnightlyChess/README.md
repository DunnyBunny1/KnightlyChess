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
  - Pseudo-legal move generation 
  - Make / Unmake moves 
### The Controller 

### The View

### Component Communication 

# The Engine
## Search & Evaluation 

### Minimax

### Alpha-beta pruning 

### Transposition Table 

### Move ordering 

## Endgame safety weights 

### Killer Move Heuristic 

### Principle Variation (PV) Nodes 

### Iterative Deepening 

### Quiescence Search  

### Piece Heat Maps
