package fake.defender

import fake.util.{HashedFile, Fake}
case class ReportedFake(file: HashedFile, fake: Fake) {}