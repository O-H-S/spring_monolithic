/*import org.hidetake.groovy.ssh.core.Remote;
import org.hidetake.groovy.ssh.Ssh;*/


/*
apply(plugin = "org.hidetake.ssh")

val remotes  = project.extensions.findByName("remotes") as NamedDomainObjectContainer<Any>
       // ?: throw IllegalStateException("Extension 'remotes' not found")

val myServer =  remotes.create("ec2-server") {
    //setProperty("host" ,"ec2.ap-northeast-2.compute.amazonaws.com")
    //user("ubuntu")
    //identity = file(".m")
}
*/

/*


val myServer =  remotes.create("ec2-server") {
    host = "ec2p-northeast-2.compute.amazonaws.com"
    user = "ubuntu"
    identity = file(".m")
}

project.tasks.create("deploy-dockerImage") {

    val sourceFile = project.rootDir.resolve("./build/docker/appImage.tar")
    doLast {
        val ssh = project.extensions.findByName("ssh") as? org.hidetake.groovy.ssh.core.Service
                ?: throw IllegalStateException("Extension 'ssh' not found")
        ssh.run(delegateClosureOf<org.hidetake.groovy.ssh.core.RunHandler> {
            session(myServer, delegateClosureOf<org.hidetake.groovy.ssh.session.SessionHandler> {
                put(hashMapOf("from" to sourceFile, "into" to "./"))
            })
        })
    }
}
*/
