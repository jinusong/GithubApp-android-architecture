package com.yjh.project.commitprogress.domain.Repository

import com.omjoonkim.project.interviewtask.model.Person
import com.omjoonkim.project.interviewtask.model.Repo
import com.yjh.project.commitprogress.network.GithubApiClient
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

class UserDataNetworkRepository(val githubApiClient: GithubApiClient) : UserDataRepository {

    override fun getUserProfile(userName: String): Single<Person> = githubApiClient.getUserProfile(userName)

    override fun getRepositories(userName: String): Single<List<Pair<Repo, List<Person>>>> = githubApiClient.getUserRepo(userName)
            .flatMap {
                Observable.fromIterable(it)
                        .flatMap {
                            Observable.zip(
                                    Observable.just(it),
                                    githubApiClient.getStargazers(it.owner.login, it.name),
                                    BiFunction<Repo, List<Person>, Pair<Repo, List<Person>>> { t1, t2 ->
                                        Pair(t1, t2)
                                    })
                        }
            }
            .toList()
}