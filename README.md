This source is intended for development of Eternal Media Bar so that users may observe the progress of the program's development, report problems, request features, and even contribute directly to the development.

<strong>This project requires Android Studio 1.5 or later<br/>
The application is intended for all android devices 3.0 and later, however currently has only been tested on 5.0 and 4.0 in x86 and ARMv7</strong>

<strong>APK builds will come when RC1 goals are met, and thereafter will be generated after each new feature is added, assuming it appears stable.</strong>

<strong> UPDATE: </strong>RC1 is complete and ready, but my tablet has bricked this week. It will be at worst 2 more weeks before I can get it upgraded or repaired, so I can do ARM(Neon) testing.
<br/>I do not want to make any releases until ARM testing has been done.

<h2>Contact us</h2>
Both Lunar Tales and Eternal Blue Flame may be found at our GitHub and our respective blogs.
You may contact us through GitHub via message, Issue report, or by placing an ask on one of our blogs.

Eternal Blue Flame: http://eternal-blue-flame.tumblr.com/ 

Lunar Tales: No link available at this time. Just use Eternal as a mediator.
<hr/>
<h2> About us</h2>
Eternal: I am a hobbyist programmer of over 8 years, I mainly use C# in Unity 3D to create games and tools, but I have an Android Launcher project in Java that I try and keep up, alongside working on my other programs. Sometimes a bit on an extrovert, as you will usually see on my blog.

Lunar Tales is my protégé, she is the one behind the graphics, whether it's making them directly, or helping me get things just right. Skilled in XHTML/CSS I hope to teach her, through our projects, other programming languages later down the road.

We love to see people contribute to our projects, any and all issues, pull requests, and ideas will be reviewed, and even if we don't use your submission directly, it may help the project in other ways.


<hr>
<h2>Project Goals:</h2>

<strong>Constant Goals:</strong>
-	Anything that will improve performance for CPU, GPU, and/or RAM takes a priority, even if it's a seemingly miniscule amount.
-	Stability takes the biggest priority.
-	Security of user information takes priority.
</hr>
<hr>
<strong>RC1 Plans: DONE</strong>
-	Cleanup code and double check for errors before release -Need to do ARM(Neon) testing, waiting on my tablet getting repaired or refunded (thank god for warranties). Estimated Wait time is 2 weeks from 12/14/2015.
-	disable hide apps until RC2 -Done but keeping note for reference.
</hr>
<hr>
<strong>RC2 Plans:</strong>
-	app boot animation overlay (plays an android boot animation while the app you want to use loads in the background)
-	auto app organizer using an XML parse of the google store's website (probably offload to an async task so the progress can be measured and a loading screen can be given to the user).
-	custom wallpaper for selected app from file, if file exists (use an async task).
-	forced radial corners on icons
-	icon backgrounds based on theme color (glass wave effect)
-	listview scroll snapping
-	menu sounds
-	extra settings:
-	change font color //absolute white disables it
-	change icon color //hmenu only //absolute white disables it
-	change menu background color //effects icon background and slide out menu color
-	enable/disable custom app backgrounds
-	download app backgrounds from google play (xml parse)
-	un-hide hidden apps //selective menu
-	enable google icons //uses icons from installed google services like play games, movies, web, settings, and music, if they are available.
-	disable app boot animation
</hr>
<hr>
<strong>RC3 plans:</strong>
-	Custom app/menu icons?
-	Widgets in the scroll list, or on the side?
-	built in music player //Separate activity for player //Separate activity for background player
-	save custom themes to file for sharing?
</hr>
<hr>
<strong>RC4 plans:</strong>
-	built in video player. //Built off VLC? //Separate Activity
-	root checker
-	CPU max clock control for root users
-	max clock control able to be changed per app.
-	Gaming mode, toggle, High battery drain warning. always max CPU frequency when on.
<hr/>



<h2>Legal Stuff</h2>


By downloading, streaming, or otherwise using this software or source code you agree to the following:
-	The source code and art assets must not to be mistaken for free software, an open source in a free-software activist understanding, copy-left or public domain software. All source code and art assets remain copyrighted and licensed by Eternal Blue Flame and Lunar Tales. And you are allowed to use them (modify, tweak, make a derivative work, distribute, etc.) only under following conditions.
-	The source code, modifications or derivative works can be distributed only if they are intended for non-commercial Use, and only if valid customers would be able to use them. You are not allowed to bypass this restriction and commercially distribute free or otherwise the code, art, or a standalone application or use our code in your projects.
-	You are not allowed to distribute original art assets (textures, models, fonts, icons, etc.)
-	You are allowed to share the source files and compiled applications with other developers (crediting us when logical/reasonable), downloading and compiling the program locally, modifying and tweaking the game locally, and even publicly forking, modifying, and tweaking the source on GitHub.
-	The origin of this software must not be misrepresented. You must not claim that you wrote the original software, or made changes that you can't prove with your pull request or error report history. 
-	Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software.
-	This notice may not be removed or altered from any source distribution. 
-	By making a pull request on our GitHub repository, you're stating that you're author of these changes or have rights to the changes you've made and you're giving us the right to use it in any way.
-	Commercial usage is allowed only after you obtain an agreement from us.
-	We reserve the right to change this license at any time with or without notice, with immediate and/or retroactive effect. We believe that what we ask is reasonable, so please don't try to bypass it. We're trying to be open and honest, and we hope people will do the same for us.
-	If there's anything legal you're wondering about that wasn't answered, ask us about it. If there's anything you don't understand or you consider confusing, please contact us about it.

For more detailed information please read our EULA.
