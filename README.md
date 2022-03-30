# QUESTER 

Ben, Jada, Julia, Nate

---

### Rough Schema:
```
users:
  username: string
  quests:
      quest_id: 
         images: 
            resource_id: local path/resource id on device??
          

quests:
  id: int
    join_code: int
    active: boolean
    date_generated: datetime
    start_time: string
    end_time: string
    location: string
    proximity: float
    icon_url: string
    activities: 
      activity_id
      ...
  
  
activities:
  id: int
    order_in_quest: int
    user_query: string
    user_price_range: int
    user_popularity: float
    user_xxx: ...
    place_id: string

places:
  id: //from google
    g_place_name: string
    g_place_formatted_address: string
    g_place_coord_lat: string
    g_place_coord:lng: string
    g_image_url: string
    g_price_level: int
    g_num_ratings: int
    g_avg_rating: int
    g_main_type: string // first element in type array is main category
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
