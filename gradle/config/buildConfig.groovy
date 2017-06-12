binaryRepository {
	url = 'http://aus02ndmms001.dev.volusion.com:8081/nexus/content/repositories'
	username = 'deployment'
	password = 'deployment123'
	name = 'releases'
}

environments {
	dev {
		keystorefile = '/usr/local/share/keystore/appkeystore.jceks'
		keystorepass = 'cowsarecool'
		logging = '/tmp'

		properties {
			services {
				route {
					muk {
						nrt{
							interval = '10s'
						}
						medium {
							interval = '60m'
						}
						sftp {
							target = 'file:/tmp/verse'
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
						clientId = 'mukapi'
						server = 'http://localhost:8080/uaa'
						tokenPath = '/oauth/token'
						userInfoPath = '/userinfo'
						checkTokenPath = '/check_token'
						//salt = 'don't set unless you know what you are doing'
					}
					paypal {
						uri = 'https://api.sandbox.paypal.com/v1'
						clientId ='ATIGpVdflSW-kHeroHS6HzNFe-jWMIBetviFFynwU1o1XBN_h_9HAP48tStmZuC65GETL7ql4Pe74_fT'
					}
					stripe {
						uri = 'https://api.stripe.com/v1'
						clientId = 'stripe'
					}
				}
				camel {
					camel {
						activemq {
							dataDirectoryFile = 'activemq-data'
						}
						jetty {
							httpPort = 8082
						}
					}
				}
			}
		}
	}

	prod {
		keystorefile = '/usr/local/share/keystore/appkeystore.jceks'
		keystorepass = 'cowsarecool'
		logging = '/tmp'

		properties {
			services {
				route {
					muk {
						nrt{
							interval = '10s'
						}
						medium {
							interval = '60m'
						}
						sftp {
							target = 'file:/tmp/verse'
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
						clientId = 'mukapi'
						server = 'https://localhost:8080/uaa'
						tokenPath = '/oauth/token'
						userInfoPath = '/userinfo'
						checkTokenPath = '/check_token'
						//salt = 'don't set unless you know what you are doing'
					}
					paypal {
						uri = 'https://api.paypal.com/v1'
						clientId ='?'
					}
					stripe {
						uri = 'https://api.stripe.com/v1'
						clientId = 'stripe'
					}
				}
				camel {
					camel {
						activemq {
							dataDirectoryFile = 'activemq-data'
						}
						jetty {
							httpPort = 8082
						}
					}
				}
			}
		}
	}
}
