#Schematics

This is an overview of how schematics get treated within this plugin.

##Process

- [Loading Schematics](#loading-schematics)
- [Splitting Schematics](#splitting-schematics)
- [Schematic Handler](#schematic-handler)
- [Schematic Pasting](#schematic-pasting)
- [Cleanup](#cleanup)

###Loading Schematics

Schematics get loaded upload the plugin being started. However, there is a 10-second wait period between the plugin actually starting and loading the schematics.
This is to ensure that the server won't throttle itself and crash trying to load all the schematics upon plugin startup.
This is a very resource intensive process currently and will slow server TPS and cause lag during schematic loading.

##Splitting Schematics

After the schematic is loaded, each schematic is split into 5 different sections. 4 Regions being the North-East region, South-East region, South-West region, and North-West region.
The last sections contains any blocks that may have somehow been missed inside each section. Each section is stored in memory for quick pasting.

##Schematic Handler

Once the schematics are loaded into memory, a quick process called the Schematic Handler is started. Every second(20 TPS) the handler will check the queue of players to see if a schematic must be pasted.

##Schematic Pasting

Schematics get pasted in the 5 different split sections mention in [Splitting Schematics](#splitting-schematics), each with a 1-second delay in-between each other.
To ensure that the server isn't pasting another schematic and may lag, each player is added to the queue in the [Schematic Handler](#schematic-handler). 
If the queue is empty, the pasting process begins immediately, and gets marked as being **Busy** until it is finished pasting the schematic. 
If the queue contains other players, the selected player must wait their turn in the queue, and will be notified upon completion.

##Cleanup

This is called only when the plugin is disabled, and sets all stored variables to null. This is to ensure that the next time the plugin is loaded it is not overloading the server's memory.