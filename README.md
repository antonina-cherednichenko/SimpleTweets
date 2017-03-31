# Project 4 - *Simple Tweets*

**Simple Tweets** is an android app that allows a user to view home and mentions timelines, view user profiles with user timelines, as well as compose and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **20** hours spent in total

## User Stories

The following **required** functionality is completed:

* [X] The app includes **all required user stories** from Week 3 Twitter Client
* [X] User can **switch between Timeline and Mention views using tabs**
  * [X] User can view their home timeline tweets.
  * [X] User can view the recent mentions of their username.
* [X] User can navigate to **view their own profile**
  * [X] User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [X] User can **click on the profile image** in any tweet to see **another user's** profile.
 * [X] User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
 * [X] Profile view includes that user's timeline
* [X] User can [infinitely paginate](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView) any of these timelines (home, mentions, user) by scrolling to the bottom

The following **optional** features are implemented:

* [ ] User can view following / followers list through the profile
* [X] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases, network failures
* [ ] When a network request is sent, user sees an [indeterminate progress indicator](http://guides.codepath.com/android/Handling-ProgressBars#progress-within-actionbar)
* [X] User can **"reply" to any tweet on their home timeline**
  * [ ] The user that wrote the original tweet is automatically "@" replied in compose
* [X] User can click on a tweet to be **taken to a "detail view"** of that tweet
 * [X] User can take favorite (and unfavorite) or retweet actions on a tweet
* [X] User can **search for tweets matching a particular query** and see results
* [X] Usernames and hashtags are styled and clickable within tweets [using clickable spans](http://guides.codepath.com/android/Working-with-the-TextView#creating-clickable-styled-spans)

The following **bonus** features are implemented:

* [X] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.com/android/Using-Parceler).
* [X] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.**ButterKnife is used instead**
* [X] On the profile screen, leverage the [CoordinatorLayout](http://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout#responding-to-scroll-events) to [apply scrolling behavior](https://hackmd.io/s/SJyDOCgU) as the user scrolls through the profile timeline.
* [ ] User can view their direct messages (or send new ones)

The following **additional** features are implemented:

* [ ] List anything else that you can get done to improve the app functionality!

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='http://i.imgur.com/link/to/your/gif/file.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android


## License
