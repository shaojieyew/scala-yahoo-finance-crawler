cd /home/ysj/Desktop/Workspace/scala_yhfinance/YahooFinance/target
java -cp  YahooFinance-1.0-SNAPSHOT-jar-with-dependencies.jar finance.App localhost:5432 postgres admin Data/Listing/ print
#java -cp  YahooFinance-1.0-SNAPSHOT-jar-with-dependencies.jar finance.App localhost:5432 postgres admin Data/Listing print
#java -cp  YahooFinance-1.0-SNAPSHOT-jar-with-dependencies.jar finance.App localhost:5432 postgres admin Data/Listing/US_LARGE_CAP.csv print seed
#java -cp  YahooFinance-1.0-SNAPSHOT-jar-with-dependencies.jar finance.App localhost:5432 postgres admin Data/Listing/SGX.csv print

#dbaddress, dbuser, dbpassword, input_file.csv, print/seed, -s g3b.si,D05.SI
#print to show debug
#seed to update the stock in seeder
#-s are specific stocks to update
