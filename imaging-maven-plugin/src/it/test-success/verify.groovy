/*
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 *
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 *
 * 
 * 
 */

import com.amazonaws.services.ec2.*
import com.amazonaws.services.ec2.model.DescribeImagesRequest
import com.amazonaws.services.ec2.model.Tag
import org.codehaus.plexus.util.IOUtil

// TODO Fix this!
File file = new File( basedir, "target" );
if ( !file.isDirectory() )
{
    throw new FileNotFoundException( "Could not find generated target directory: " + file );
}

File buildLog = new File( basedir, "build.log" );
if ( !buildLog.exists() )
{
    System.err.println( buildLog.getAbsolutePath() + " is missing." );
    return false;
}

String content = IOUtil.toString( new FileInputStream( buildLog ) );
String buidSuccess = "amazon-ebs: AMIs were created";
if ( content.contains( buidSuccess ) )
{
    System.err.println( "build.log contains '" + buidSuccess + "'" );

    // Now check if the new AMI has the tag added to it.
    println "Connecting to AWS"

    def index = content.indexOf(buidSuccess)
    def amiId = content.substring(index+45, index+58 ).trim()
    println("AMI created "+amiId)
    def region = "us-west-2"
    def ec2 = AmazonEC2ClientBuilder.standard().withRegion(region).build()
    def result = ec2.describeImages(new DescribeImagesRequest().withImageIds(amiId))
    def tags = result.getImages().get(0).tags
    boolean containsItTag = false;
    for (Tag tag: tags) {
        if(tag.key.equalsIgnoreCase("itTag") && tag.value.equalsIgnoreCase("itValue")) containsItTag = true
    }
    println("Found the tag "+containsItTag)
    return containsItTag
}

System.err.println("_------------------------------------  FAIL!");
return false;