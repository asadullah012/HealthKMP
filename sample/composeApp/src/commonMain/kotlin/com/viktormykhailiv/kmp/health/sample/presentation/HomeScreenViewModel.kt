package com.viktormykhailiv.kmp.health.sample.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viktormykhailiv.kmp.health.HealthDataType
import com.viktormykhailiv.kmp.health.records.BloodPressureRecord
import com.viktormykhailiv.kmp.health.records.ExerciseSessionRecord
import com.viktormykhailiv.kmp.health.records.HeartRateRecord
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.StepsRecord
import com.viktormykhailiv.kmp.health.region.RegionalPreferences
import com.viktormykhailiv.kmp.health.sample.domain.HealthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

// New data holder (replaces HealthState from HealthViewModel)
data class HealthData(
    val heartRate: List<HeartRateRecord> = emptyList(),
    val steps: List<StepsRecord> = emptyList(),
    val bloodPressure: List<BloodPressureRecord> = emptyList(),
    val sleepData: List<SleepSessionRecord> = emptyList(),
    val exerciseHistory: List<ExerciseSessionRecord> = emptyList(),
)

data class HealthConfig(
    val revokeSupported: Boolean,
    val backgroundSyncSupported: Boolean,
    val regionalPreferences: RegionalPreferences?,
)

class HomeScreenViewModel(
    private val healthRepository: HealthRepository,
) : ViewModel() {

    private val _readTypes = MutableStateFlow<MutableSet<HealthDataType>>(mutableSetOf())
    private val _writeTypes = MutableStateFlow<MutableSet<HealthDataType>>(mutableSetOf())
    private val _healthConfig = MutableStateFlow<HealthConfig?>(null)
    private val _isHealthAvailable = MutableStateFlow<Boolean?>(null)
    private val _healthData = MutableStateFlow(HealthData())  // new

    private val _authRefreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val authorizationStatus: StateFlow<AuthStatus> = _authRefreshTrigger
        .flatMapLatest {
            flow {
                val isAuthorized = healthRepository
                    .isAuthorized(_readTypes.value.toList(), _writeTypes.value.toList())
                    .getOrNull() ?: false
                emit(if (isAuthorized) AuthStatus.Authorized else AuthStatus.NotAuthorized)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthStatus.Unknown)

    @OptIn(ExperimentalCoroutinesApi::class)
    val backgroundAuthorizationStatus: StateFlow<AuthStatus> = _authRefreshTrigger
        .flatMapLatest {
            flow {
                val hasPermission = healthRepository
                    .hasReadHealthDataInBackgroundPermission()
                    .getOrNull() ?: false
                emit(if (hasPermission) AuthStatus.Authorized else AuthStatus.NotAuthorized)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthStatus.Unknown)

    val uiState: StateFlow<HomeUiState> = combine(
        _isHealthAvailable,
        _healthConfig,
        authorizationStatus,
        backgroundAuthorizationStatus,
        _healthData,  // new
    ) { isAvailable, config, authStatus, backgroundAuthStatus, healthData ->
        when {
            isAvailable == null -> HomeUiState.Loading
            !isAvailable -> HomeUiState.HealthNotAvailable
            config == null -> HomeUiState.Loading
            else -> HomeUiState.Ready(
                revokeSupported = config.revokeSupported,
                backgroundSyncSupported = config.backgroundSyncSupported,
                foregroundAuth = authStatus,
                backgroundAuth = backgroundAuthStatus,
                regionalPreferences = config.regionalPreferences,
                healthData = healthData,  // new
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState.Loading)

    init {
        checkHealthAvailability()
        updateReadAndWriteType(
            readTypes = listOf(
                HealthDataType.HeartRate,
                HealthDataType.Exercise(),
                HealthDataType.Sleep,
                HealthDataType.Steps,
            ),
            writeTypes = emptyList(),
        )
        syncData()  // new
    }

    fun onResume() {
        viewModelScope.launch {
            _authRefreshTrigger.emit(Unit)
        }
        syncData()  // new — refresh health data on every resume
    }

    // ── Health data sync (ported from HealthViewModel) ────────────────────────

    fun syncData() {
        syncHeartRate()
        syncSteps()
        syncSleep()
        syncExercise()
        syncBloodPressure()
    }

    private fun syncHeartRate() {
        viewModelScope.launch {
            healthRepository.readHeartRate(
                startTime = getStartOfToday(),
                endTime = Clock.System.now(),
            ).onSuccess { records ->
                _healthData.update { it.copy(heartRate = records) }
                println("syncHeartRate $records")
            }.onFailure {
                println("Failed to read heart rate: $it")
            }
        }
    }

    private fun syncSteps() {
        viewModelScope.launch {
            healthRepository.readSteps(
                startTime = getStartOfToday(),
                endTime = Clock.System.now(),
            ).onSuccess { records ->
                _healthData.update { it.copy(steps = records) }
                println("syncSteps $records")
            }.onFailure {
                println("Failed to read steps: $it")
            }
        }
    }

    private fun syncSleep() {
        viewModelScope.launch {
            healthRepository.readSleep(
                startTime = getStartOfToday(),
                endTime = Clock.System.now(),
            ).onSuccess { records ->
                _healthData.update { it.copy(sleepData = records) }
                println("syncSleep $records")
            }.onFailure {
                println("Failed to read sleep: $it")
            }
        }
    }

    private fun syncExercise() {
        viewModelScope.launch {
            healthRepository.readExercise(
                startTime = getStartOfToday(),
                endTime = Clock.System.now(),
            ).onSuccess { records ->
                _healthData.update { it.copy(exerciseHistory = records) }
                println("syncExercise $records")
            }.onFailure {
                println("Failed to read exercise: $it")
            }
        }
    }

    private fun syncBloodPressure() {
        viewModelScope.launch {
            healthRepository.readBloodPressure(
                startTime = getStartOfToday(),
                endTime = Clock.System.now(),
            ).onSuccess { records ->
                _healthData.update { it.copy(bloodPressure = records) }
                println("syncBloodPressure $records")
            }.onFailure {
                println("Failed to read blood pressure: $it")
            }
        }
    }

    private fun getStartOfToday(): Instant {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        return today.atStartOfDayIn(TimeZone.currentSystemDefault())
    }

    // ── Existing methods (unchanged) ──────────────────────────────────────────

    private fun updateReadAndWriteType(
        readTypes: List<HealthDataType>,
        writeTypes: List<HealthDataType>,
    ) {
        _readTypes.value.addAll(readTypes)
        _writeTypes.value.addAll(writeTypes)
    }

    private fun checkHealthAvailability() {
        viewModelScope.launch {
            val isAvailable = healthRepository.isAvailable().getOrNull() ?: false
            _isHealthAvailable.value = isAvailable
            if (!isAvailable) return@launch

            _healthConfig.value = HealthConfig(
                revokeSupported = healthRepository.isRevokeAuthorizationSupported()
                    .getOrNull() ?: false,
                backgroundSyncSupported = healthRepository.isBackgroundSyncSupported()
                    .getOrNull() ?: false,
                regionalPreferences = healthRepository.getRegionalPreferences().getOrNull(),
            )
        }
    }

    fun requestAuthorization() {
        viewModelScope.launch {
            healthRepository.requestAuthorization(
                readTypes = _readTypes.value.toList(),
                writeTypes = _writeTypes.value.toList(),
            )
        }
    }

    fun requestAuthorizationWithBackground() {
        viewModelScope.launch {
            healthRepository.requestAuthorizationWithBackground(
                readTypes = _readTypes.value.toList(),
                writeTypes = _writeTypes.value.toList(),
            )
        }
    }

    fun revokeAuthorization() {
        viewModelScope.launch {
            healthRepository.revokeAuthorization()
                .onSuccess { _authRefreshTrigger.emit(Unit) }
                .onFailure { _authRefreshTrigger.emit(Unit) }
        }
    }

    fun requestBackgroundPermission() {
        viewModelScope.launch {
            healthRepository.requestReadHealthDataInBackgroundPermission()
            _authRefreshTrigger.emit(Unit)
        }
    }
}

// ── UI state ──────────────────────────────────────────────────────────────────

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object HealthNotAvailable : HomeUiState
    data class Ready(
        val revokeSupported: Boolean,
        val backgroundSyncSupported: Boolean,
        val regionalPreferences: RegionalPreferences?,
        val foregroundAuth: AuthStatus,
        val backgroundAuth: AuthStatus,
        val healthData: HealthData,  // new
    ) : HomeUiState
}

sealed interface AuthStatus {
    data object Unknown : AuthStatus
    data object NotAuthorized : AuthStatus
    data object Authorized : AuthStatus
}