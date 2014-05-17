package tb_backend.model

case class Login(username: String, password: String)
case class LoginResponse(uid: Long, token: String)
case class Register(username: String, password: String, email: String, connector: String)


// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  
  val profile = scala.slick.driver.PostgresDriver
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = ActivityDiary.ddl ++ Auth.ddl ++ Connectors.ddl ++ MoodScales.ddl ++ Programme.ddl ++ ScheduledTasks.ddl ++ User.ddl
  
  /** Entity class storing rows of table ActivityDiary
   *  @param aid Database column aid AutoInc, PrimaryKey
   *  @param uid Database column uid 
   *  @param day Database column day 
   *  @param activity Database column activity 
   *  @param expMood Database column exp_mood 
   *  @param achMood Database column ach_mood 
   *  @param satisfaction Database column satisfaction 
   *  @param achievement Database column achievement 
   *  @param note Database column note  */
  case class ActivityDiaryRow(aid: Long, uid: Long, day: Int, activity: Option[String], expMood: Option[Int], achMood: Option[Int], satisfaction: Option[Int], achievement: Option[Int], note: Option[String])
  /** GetResult implicit for fetching ActivityDiaryRow objects using plain SQL queries */
  implicit def GetResultActivityDiaryRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[Option[String]], e3: GR[Option[Int]]): GR[ActivityDiaryRow] = GR{
    prs => import prs._
    ActivityDiaryRow.tupled((<<[Long], <<[Long], <<[Int], <<?[String], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[String]))
  }
  /** Table description of table activity_diary. Objects of this class serve as prototypes for rows in queries. */
  class ActivityDiary(tag: Tag) extends Table[ActivityDiaryRow](tag, "activity_diary") {
    def * = (aid, uid, day, activity, expMood, achMood, satisfaction, achievement, note) <> (ActivityDiaryRow.tupled, ActivityDiaryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (aid.?, uid.?, day.?, activity, expMood, achMood, satisfaction, achievement, note).shaped.<>({r=>import r._; _1.map(_=> ActivityDiaryRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7, _8, _9)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column aid AutoInc, PrimaryKey */
    val aid: Column[Long] = column[Long]("aid", O.AutoInc, O.PrimaryKey)
    /** Database column uid  */
    val uid: Column[Long] = column[Long]("uid")
    /** Database column day  */
    val day: Column[Int] = column[Int]("day")
    /** Database column activity  */
    val activity: Column[Option[String]] = column[Option[String]]("activity")
    /** Database column exp_mood  */
    val expMood: Column[Option[Int]] = column[Option[Int]]("exp_mood")
    /** Database column ach_mood  */
    val achMood: Column[Option[Int]] = column[Option[Int]]("ach_mood")
    /** Database column satisfaction  */
    val satisfaction: Column[Option[Int]] = column[Option[Int]]("satisfaction")
    /** Database column achievement  */
    val achievement: Column[Option[Int]] = column[Option[Int]]("achievement")
    /** Database column note  */
    val note: Column[Option[String]] = column[Option[String]]("note")
  }
  /** Collection-like TableQuery object for table ActivityDiary */
  lazy val ActivityDiary = new TableQuery(tag => new ActivityDiary(tag))
  
  /** Entity class storing rows of table Auth
   *  @param uid Database column uid 
   *  @param token Database column token  */
  case class AuthRow(uid: Long, token: Option[String])
  /** GetResult implicit for fetching AuthRow objects using plain SQL queries */
  implicit def GetResultAuthRow(implicit e0: GR[Long], e1: GR[Option[String]]): GR[AuthRow] = GR{
    prs => import prs._
    AuthRow.tupled((<<[Long], <<?[String]))
  }
  /** Table description of table auth. Objects of this class serve as prototypes for rows in queries. */
  class Auth(tag: Tag) extends Table[AuthRow](tag, "auth") {
    def * = (uid, token) <> (AuthRow.tupled, AuthRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (uid.?, token).shaped.<>({r=>import r._; _1.map(_=> AuthRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column uid  */
    val uid: Column[Long] = column[Long]("uid")
    /** Database column token  */
    val token: Column[Option[String]] = column[Option[String]]("token")
    
    /** Uniqueness Index over (uid) (database name CONSTRAINT_INDEX_2) */
    val index1 = index("CONSTRAINT_INDEX_2", uid, unique=true)
  }
  /** Collection-like TableQuery object for table Auth */
  lazy val Auth = new TableQuery(tag => new Auth(tag))
  
  /** Entity class storing rows of table Connectors
   *  @param uid Database column uid 
   *  @param connector Database column connector 
   *  @param cType Database column c_type  */
  case class ConnectorsRow(uid: Long, connector: Option[String], cType: Option[Int])
  /** GetResult implicit for fetching ConnectorsRow objects using plain SQL queries */
  implicit def GetResultConnectorsRow(implicit e0: GR[Long], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[ConnectorsRow] = GR{
    prs => import prs._
    ConnectorsRow.tupled((<<[Long], <<?[String], <<?[Int]))
  }
  /** Table description of table connectors. Objects of this class serve as prototypes for rows in queries. */
  class Connectors(tag: Tag) extends Table[ConnectorsRow](tag, "connectors") {
    def * = (uid, connector, cType) <> (ConnectorsRow.tupled, ConnectorsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (uid.?, connector, cType).shaped.<>({r=>import r._; _1.map(_=> ConnectorsRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column uid  */
    val uid: Column[Long] = column[Long]("uid")
    /** Database column connector  */
    val connector: Column[Option[String]] = column[Option[String]]("connector")
    /** Database column c_type  */
    val cType: Column[Option[Int]] = column[Option[Int]]("c_type")
    
    /** Uniqueness Index over (uid) (database name CONSTRAINT_INDEX_D) */
    val index1 = index("CONSTRAINT_INDEX_D", uid, unique=true)
  }
  /** Collection-like TableQuery object for table Connectors */
  lazy val Connectors = new TableQuery(tag => new Connectors(tag))
  
  /** Entity class storing rows of table MoodScales
   *  @param mid Database column mid AutoInc, PrimaryKey
   *  @param uid Database column uid 
   *  @param posContacts Database column pos_contacts 
   *  @param negContacts Database column neg_contacts 
   *  @param posActivities Database column pos_activities 
   *  @param negActivities Database column neg_activities 
   *  @param posThoughts Database column pos_thoughts 
   *  @param negThoughts Database column neg_thoughts  */
  case class MoodScalesRow(mid: Long, uid: Long, posContacts: Option[Int], negContacts: Option[Int], posActivities: Option[Int], negActivities: Option[Int], posThoughts: Option[Int], negThoughts: Option[Int])
  /** GetResult implicit for fetching MoodScalesRow objects using plain SQL queries */
  implicit def GetResultMoodScalesRow(implicit e0: GR[Long], e1: GR[Option[Int]]): GR[MoodScalesRow] = GR{
    prs => import prs._
    MoodScalesRow.tupled((<<[Long], <<[Long], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table mood_scales. Objects of this class serve as prototypes for rows in queries. */
  class MoodScales(tag: Tag) extends Table[MoodScalesRow](tag, "mood_scales") {
    def * = (mid, uid, posContacts, negContacts, posActivities, negActivities, posThoughts, negThoughts) <> (MoodScalesRow.tupled, MoodScalesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (mid.?, uid.?, posContacts, negContacts, posActivities, negActivities, posThoughts, negThoughts).shaped.<>({r=>import r._; _1.map(_=> MoodScalesRow.tupled((_1.get, _2.get, _3, _4, _5, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column mid AutoInc, PrimaryKey */
    val mid: Column[Long] = column[Long]("mid", O.AutoInc, O.PrimaryKey)
    /** Database column uid  */
    val uid: Column[Long] = column[Long]("uid")
    /** Database column pos_contacts  */
    val posContacts: Column[Option[Int]] = column[Option[Int]]("pos_contacts")
    /** Database column neg_contacts  */
    val negContacts: Column[Option[Int]] = column[Option[Int]]("neg_contacts")
    /** Database column pos_activities  */
    val posActivities: Column[Option[Int]] = column[Option[Int]]("pos_activities")
    /** Database column neg_activities  */
    val negActivities: Column[Option[Int]] = column[Option[Int]]("neg_activities")
    /** Database column pos_thoughts  */
    val posThoughts: Column[Option[Int]] = column[Option[Int]]("pos_thoughts")
    /** Database column neg_thoughts  */
    val negThoughts: Column[Option[Int]] = column[Option[Int]]("neg_thoughts")
  }
  /** Collection-like TableQuery object for table MoodScales */
  lazy val MoodScales = new TableQuery(tag => new MoodScales(tag))
  
  /** Entity class storing rows of table Programme
   *  @param day Database column day 
   *  @param image Database column image 
   *  @param sentence Database column sentence 
   *  @param activityS Database column activity_s 
   *  @param activityL Database column activity_l 
   *  @param activitySB Database column activity_s_b 
   *  @param activityLB Database column activity_l_b  */
  case class ProgrammeRow(day: Int, image: Option[String], sentence: Option[String], activityS: Option[String], activityL: Option[String], activitySB: Option[String], activityLB: Option[String])
  /** GetResult implicit for fetching ProgrammeRow objects using plain SQL queries */
  implicit def GetResultProgrammeRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[ProgrammeRow] = GR{
    prs => import prs._
    ProgrammeRow.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table programme. Objects of this class serve as prototypes for rows in queries. */
  class Programme(tag: Tag) extends Table[ProgrammeRow](tag, "programme") {
    def * = (day, image, sentence, activityS, activityL, activitySB, activityLB) <> (ProgrammeRow.tupled, ProgrammeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (day.?, image, sentence, activityS, activityL, activitySB, activityLB).shaped.<>({r=>import r._; _1.map(_=> ProgrammeRow.tupled((_1.get, _2, _3, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column day  */
    val day: Column[Int] = column[Int]("day")
    /** Database column image  */
    val image: Column[Option[String]] = column[Option[String]]("image")
    /** Database column sentence  */
    val sentence: Column[Option[String]] = column[Option[String]]("sentence")
    /** Database column activity_s  */
    val activityS: Column[Option[String]] = column[Option[String]]("activity_s")
    /** Database column activity_l  */
    val activityL: Column[Option[String]] = column[Option[String]]("activity_l")
    /** Database column activity_s_b  */
    val activitySB: Column[Option[String]] = column[Option[String]]("activity_s_b")
    /** Database column activity_l_b  */
    val activityLB: Column[Option[String]] = column[Option[String]]("activity_l_b")
    
    /** Uniqueness Index over (day) (database name CONSTRAINT_INDEX_C) */
    val index1 = index("CONSTRAINT_INDEX_C", day, unique=true)
  }
  /** Collection-like TableQuery object for table Programme */
  lazy val Programme = new TableQuery(tag => new Programme(tag))
  
  /** Entity class storing rows of table ScheduledTasks
   *  @param tid Database column tid AutoInc, PrimaryKey
   *  @param uid Database column uid 
   *  @param tType Database column t_type 
   *  @param day Database column day 
   *  @param time Database column time  */
  case class ScheduledTasksRow(tid: Long, uid: Long, tType: Int, day: Option[Int], time: Option[Int])
  /** GetResult implicit for fetching ScheduledTasksRow objects using plain SQL queries */
  implicit def GetResultScheduledTasksRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[Option[Int]]): GR[ScheduledTasksRow] = GR{
    prs => import prs._
    ScheduledTasksRow.tupled((<<[Long], <<[Long], <<[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table scheduled_tasks. Objects of this class serve as prototypes for rows in queries. */
  class ScheduledTasks(tag: Tag) extends Table[ScheduledTasksRow](tag, "scheduled_tasks") {
    def * = (tid, uid, tType, day, time) <> (ScheduledTasksRow.tupled, ScheduledTasksRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (tid.?, uid.?, tType.?, day, time).shaped.<>({r=>import r._; _1.map(_=> ScheduledTasksRow.tupled((_1.get, _2.get, _3.get, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column tid AutoInc, PrimaryKey */
    val tid: Column[Long] = column[Long]("tid", O.AutoInc, O.PrimaryKey)
    /** Database column uid  */
    val uid: Column[Long] = column[Long]("uid")
    /** Database column t_type  */
    val tType: Column[Int] = column[Int]("t_type")
    /** Database column day  */
    val day: Column[Option[Int]] = column[Option[Int]]("day")
    /** Database column time  */
    val time: Column[Option[Int]] = column[Option[Int]]("time")
  }
  /** Collection-like TableQuery object for table ScheduledTasks */
  lazy val ScheduledTasks = new TableQuery(tag => new ScheduledTasks(tag))
  
  /** Entity class storing rows of table User
   *  @param uid Database column uid AutoInc, PrimaryKey
   *  @param email Database column email 
   *  @param username Database column username 
   *  @param password Database column password 
   *  @param firstname Database column firstname 
   *  @param lastname Database column lastname  */
  case class UserRow(uid: Long, email: String, username: String, password: String, firstname: Option[String] = None, lastname: Option[String] = None)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[String]]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Long], <<[String], <<[String], <<[String], <<?[String], <<?[String]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(tag: Tag) extends Table[UserRow](tag, "user") {
    def * = (uid, email, username, password, firstname, lastname) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (uid.?, email.?, username.?, password.?, firstname, lastname).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column uid AutoInc, PrimaryKey */
    val uid: Column[Long] = column[Long]("uid", O.AutoInc, O.PrimaryKey)
    /** Database column email  */
    val email: Column[String] = column[String]("email")
    /** Database column username  */
    val username: Column[String] = column[String]("username")
    /** Database column password  */
    val password: Column[String] = column[String]("password")
    /** Database column firstname  */
    val firstname: Column[Option[String]] = column[Option[String]]("firstname")
    /** Database column lastname  */
    val lastname: Column[Option[String]] = column[Option[String]]("lastname")
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}