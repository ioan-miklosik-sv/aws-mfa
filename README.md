Small utility to automate the update of aws credentials file using the aws assume-role command

Usage: java -jar path-to-this-jar role-arn your-arn profile auth-app-code

The program will update the [default] profile of your ~/.aws/credentials file with new temporary tokens based on the role arn, your user arn, selected profile and the code from an auth tool like google authenticator.
It will not touch other profiles in your credentials file.
The credentials file must be already there and it must contain the 3 properties already initialized with some values like this:

[default]
aws_access_key_id=ASIAFZHMNLPJJJPUWKKQ
aws_secret_access_key=DR04S0LbszJcvOIIkuv2DGZhDC8xU6bjx2MyhMNP
aws_session_token=BQoDYJdzEC4aDD/cKRCZOUqC6zgmxyLoAYJQhBhtrIwLculzld09A9015Qi4TLfzSTZiRSV70x6UUX2KgOT6gzGOrIzIImBZ4fHXi2fTV7OxVld2khge2VOvyLFHHpOCMrhPz54z13ikLxEnqRRxSjqYY4j9AmN4+qeQXGnAQrIym/0Cf+JCL/8crr2j3gU+jtLXS1UM/NIEqJ97XPL+VM7uyCfxG899VYyLyUfz3ljFjJuE9AvHSrq3CQ2eZahHjW09R/a43kIGPUfXWooDNZT0qq8LLq0mEEE2q0EM1/M28ADUpZQDtBtWiSre7CudTqVA6Zk9teda+vCBNRGKrngogLMf1qQ=
