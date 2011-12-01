import fake.defender._
import fake.util._

import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.nio.file.Paths

val data = """<?xml version="1.0" encoding="utf-8" standalone="yes"?>
<FileListing Version="1" CID="QDM5KPQWER4HYOCXMSFFW6DOULLU5SUROMDMLCQ" Base="/" Generator="DC++ 0.7091">
<Directory Name="software">
	<Directory Name="development">
		<Directory Name="MySQL Manager 3.7.0.1">
			<File Name="file_id.diz" Size="508" TTH="ESXAM37PQMBXL6NYPS3L2GIQ37FZZ6WWQMH5O6Y"/>
			<File Name="MyManagerPro.exe" Size="16427520" TTH="4DJNVA63O7ZOVQ4JBHYUKFM2EFSLM5AN3ND3DWA"/>
			<File Name="ssg.nfo" Size="7495" TTH="DF5VDKFIBBLHYBBE2EZY2SOMQCOR7GASV6DVMQQ"/>
		</Directory>
		<Directory Name="Visual Studio 2008 Express Edition">
			<Directory Name="VCExpress">
				<Directory Name="WCU">
					<Directory Name="RDBG">
						<Directory Name="x64">
							<File Name="expdbgsetup.exe" Size="3974144" TTH="USPRPDFBHBLYS6VZAOLMJ3PVUYTBR4ZAVFXHRKA"/>
						</Directory>
					</Directory>
				</Directory>
			</Directory>
		</Directory>
	</Directory>
</Directory>

</FileListing>
""".getBytes(Charset.forName("UTF-8"))

val shares = Map(("software", Paths.get("d:", "soft")))
val parsed = new FileListParser(shares).parseStream(new ByteArrayInputStream(data)).toArray
assert(parsed(0).path == Paths.get("software", "development", "MySQL Manager 3.7.0.1", "file_id.diz"))
assert(parsed(3).path == Paths.get("software", "development", "Visual Studio 2008 Express Edition", "VCExpress", "WCU", "RDBG", "x64", "expdbgsetup.exe"))