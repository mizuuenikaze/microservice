binaryRepository {
	url = 'http://aus02ndmms001.dev.volusion.com:8081/nexus/content/repositories'
	username = 'deployment'
	password = 'deployment123'
	name = 'releases'
}

environments {
	fcdev {
		server {
			hostname = 'target.mozu.com'
			port = 80
			context = 'mozuExtension'
			username = 'jenkins'
			password = 'jenkins'
		}

		keystorefile = '/var/lib/tomcat-9/conf/appkeystore.jceks'
		keystorepass = 'cowsarecool'

		properties {
			services {
				route {
					mozu {
						nrt{
							interval = '10s'
						}
						medium {
							interval = '60m'
						}
					}
					sstack {
						sftp {
							target = 'file:/tmp/sstack'
						}
					}
				}
			}
			webapp {
				security {
					app {
						principals = 'dXNlcjpwYXNzd29yZA==,dW5rbm93bjpuYQ=='
					}
					oauth {
						clientId = 'sstackOrder'
						server = 'https://localhost/uaa'
						tokenPath = '/oauth/token'
						userInfoPath = '/oauth/userinfo'
						checkTokenPath = '/checktoken'
						//salt = 'don't set unless you know what you are doing'
					}
				}
				camel {
					camel {
						activemq {
							dataDirectoryFile = 'activemq-data'
						}
					}
				}
			}
		}
	}
}

