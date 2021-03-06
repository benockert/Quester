# QUESTER 

Ben, Jada, Julia, Nate

---

### ~~Rough Schema:~~
### Current Schema
*may change as needed
```
users:
  username: string
  quests:
      quest_id: 
         images: 
            resource_id: local path/resource id on device??
          

quests:
  id: datetime_username
    join_code: int
    active: boolean
    location: string // might also be coordinates, depending on if user inputs location or uses current
    proximity: float
    photoReference: string // comes from google places
    currentActivity: int // represents currently active activity
    users:
      user_id
      ...
    activities: 
      id: a#
        user_query: string
        user_price_level: int
        user_popularity: int
        g_place_name: string
        g_place_id: string
        g_place_formatted_address: string
        g_place_coord_lat: string
        g_place_coord:lng: string
        g_photo_reference: string
        g_price_level: int 
      ...        

```

For each user inputted activity:

- Request sent to Places Text Search: 
  - text of query
  - minprice, maxprice, radius filter
  - time?

- Response received:
  - filter by "OPERATIONAL" status 
  - filter by popularity (stars/num ratings)
  - with remaining, select random 

After activity metadata is recieved:
1. Display generated quest to users
2. Add 'Reroll' button to each generated activity card
3. Potential: allow drag and reorder (updates order in activity object)
4. 'Confirm' button persists quest and activity data to Firebase 
    - also add random (or first, for simplicity) image from activities to be the icon image in quest
    - redirects to active quest screen, which then pulls from Firebase

- 'Finish' quest button sets active? to false and redirects to Recap screen
- 'Cancel' also sets active? to false and redirects to home Quests screen

My Quests screen:
- pulls quest ids from Firebase with logged in username/email/id
- Active quests section based on active? of each quest
- 'Past Quests' section for inactive quests

---
Hit List:
- User Login
- Pages
  - Login
  - Forgot Password
  - My Quests (homepage with active and previous)
  - Create A Quest
  - Confirm/Edit Quest
  - Selected Quest (when clicking on past quest from homepage; might be similar to Confirm quest page as in displaying all activities in recycler view)
  - Active Quest
  - Recap

My Quests Page:
- Active quest(s) at top
- Previous quests underneath sorted in descending date order
- Each quest card shows:
  - Users typed in location
  - Cover image based on that location
  - Number of participants
  - Date of quest
  - Number of stops

Active Quest:
- current 
- next

View Selected Quest:
- Show all activities
- Show participant names
- If has a recap:
  - button at top to 'View recap'
