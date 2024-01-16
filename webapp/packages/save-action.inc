#set ($obj = $packages)
#if ($form_action=="edit")
#set ($r=$obj.load($params.key))
#end
#set ($obj.name=$params.name)
#set ($obj.description = $!params.description)
#set ($obj.version = $!params.version)
#set ($obj.arch = $!params.arch)
#if($params.install)
#set ($obj.install = true)
#else
#set ($obj.install = false)
#end
#if($form_action=="new")
#set($pl=$packagelists)
#set($r=$pl.load($params.list))
#set($r=$pl.addPackage($obj))
#set($r=$pl.removeApproval())
#else
$obj.save()
#set($pl=$obj.getPackageList())
#set($r=$pl.removeApproval())
#end
#return_to_index($module)
