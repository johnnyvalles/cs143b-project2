/bin/rm -f *.class
/bin/rm -rf ~/cs143b-project2-submission-vallesja
/bin/rm -f ~/output-dp.txt
/bin/rm -f ~/cs143b-project2-submission-vallesja.zip
/bin/mkdir ~/cs143b-project2-submission-vallesja
/bin/cp *.java ~/cs143b-project2-submission-vallesja
/bin/cp README.md ~/cs143b-project2-submission-vallesja

javac *.java && java VMMDriver test/dp/init-dp.txt test/dp/input-dp.txt > output-dp.txt
/bin/cp ./output-dp.txt ~/
/bin/rm -f *.class
/bin/rm -f output-dp.txt