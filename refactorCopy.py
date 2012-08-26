#!/usr/bin/python

import os;
import os.path;
import refactorSettings;

# refactor settings only needs two variables defined. As an example:
# (destination is taken from the level above this file (so outside the submodule))
#
#destination = ["test","alpha"]
#package_replacement = ["com.oreilly.common", "test.alpha"]


if __name__ == "__main__":
    file_list = []
    files = os.walk( os.path.join( os.path.dirname(__file__), "src","com","oreilly","common"))
    local_source_path = "src" + os.sep + "com" + os.sep + "oreilly" + os.sep + "common"
    destination_path = os.path.join( os.path.dirname( __file__), os.pardir)
    for d in refactorSettings.destination:
        destination_path = os.path.join( destination_path, d )
    for item in files:
        for f in item[2]:
            local_dest_dir = item[0].replace("./", "").replace(local_source_path, destination_path)
            if not os.path.exists( local_dest_dir ):
                os.makedirs( local_dest_dir )
            if f[-5:] == ".java":
                source_file_name = item[0] + os.sep + f 
                dest_file_name = item[0].replace("./", "").replace(local_source_path, destination_path) + os.sep + f
                source_file = open( source_file_name )
                destination_file = open( dest_file_name, 'w+')
                for line in source_file:
                    processed_line =  line.replace( refactorSettings.package_replacement[0], refactorSettings.package_replacement[1])
                    destination_file.write( processed_line )
                destination_file.close()
                source_file.close()
    
            
        
        
    
