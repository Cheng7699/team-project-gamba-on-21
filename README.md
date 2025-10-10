# We put it all on 21 

We be doing a Gambling app because it will be very funny

### The following is the initial draft and all things are up for changes

## Description
#### Core Concept
App for playing classic casino games like blackjack, poker, roulette, etc. May axe some games if we don't have time.

App interacts with the user via a GUI (switch) with a main interface that shows remaining funds, and options to start
playing each of the three games. Clicking on a "Add Funds" button would lead to a page where the user can add funds
to their balance. Clicking the games would lead to the game interface.

Each game should function following their real-life counterpart, with bets placed deducting from the user's balance
accordingly immediately when placed.
After games, winnings should be added to their balance and user given options to either continue or go back to Main 
Menu.

After each action that causes a change in balance, the corresponding information should be store to the JSON file so
to prevent actions like Alt-F4s to cheat the game.

#### Additional things: (funny)

- TEAM Gambling: User A teams up with User B, they have a team balance that is deduced when either is placing bets.
B can see A's wins or losses to the team balance and A can leave messages to B when they log off. 
(This can be simplified if it is too complicated for implementation)
  - Accompanying User creation and login systems. We can make this simple due to workload and time.
  - These should all be saved locally to JSON and read when users log in.

## Key Points
- [ ] API usage: [Deck of Cards](https://deckofcardsapi.com/) (Found on course Quercus. 
Please share if you have a more interesting api)
- [ ] Saving to JSON
- [ ] GUI interactions
- [ ] Handling Games

# User Stories
- As a User, I want to top up my funds after losing it all on black.
- As a User, I want to start a game so I can put it all on back

#### TODO: (You reading this file) Add more user stories!!

# Keep this up-to-date with information about the project

The readme should include information such as:
- a summary of what your application is all about
- a list of the user stories, along with who is responsible for each one
- information about the API(s) that your project uses 
- screenshots or animations demonstrating current functionality

By keeping this README up-to-date,
your team will find it easier to prepare for the final presentation
at the end of the term.
