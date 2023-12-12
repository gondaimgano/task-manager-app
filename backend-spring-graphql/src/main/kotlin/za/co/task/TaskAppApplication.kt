package za.co.task

import jakarta.persistence.*
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@SpringBootApplication
class TaskAppApplication(
	val userRepository: UserRepository,
	val taskRepository: TaskRepository
) : CommandLineRunner{



	override fun run(vararg args: String?) {
		val u =UserFoo(0,"Gondai","gondaimgano@gmail.com")
		userRepository.saveAll(mutableListOf(u))
		taskRepository.saveAll(mutableListOf(TaskFoo(0,"Task A",u),
			TaskFoo(0,"Task B",u),
			TaskFoo(0,"Task C",u),
			TaskFoo(0,"Task D",u)))
	}

}

fun main(args: Array<String>) {
	runApplication<TaskAppApplication>(*args)
}

@Controller
class TaskController(
	private val taskRepository: TaskRepository,
	private val userRepository: UserRepository
){

	@QueryMapping
	fun findAllTaskByUser(@Argument id: Long): List<TaskFoo> {
        return taskRepository.findAllByUserId(id)
	}
	@QueryMapping
	fun findUser(@Argument id: Long): UserFoo? {
       return userRepository.findById(id).orElse(null)
	}

	@QueryMapping
	fun findByTask(@Argument id: Long): TaskFoo? {
         return taskRepository.findById(id).orElse(null)
	}
}


@Entity
class UserFoo(
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	val id: Long? = null,
	val username: String,
	val email: String,
	@OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
	val tasks: List<TaskFoo> = mutableListOf()
)


@Entity
class TaskFoo(
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	val id: Long? = null,
	val description: String,
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	val user: UserFoo
)


interface UserRepository : JpaRepository<UserFoo, Long>

interface TaskRepository : JpaRepository<TaskFoo, Long> {
	fun findAllByUserId(userId: Long): List<TaskFoo>
}


