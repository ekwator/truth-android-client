## Truth Android Client — API Update to v0.4.0-pre

This update aligns the Android client with Truth Core v0.4.0-pre and introduces the Collective Intelligence Layer.

### Added field
- `collective_score` (Double, nullable): Collective truth estimation in range [0, 1]. Present on event objects returned by the core when computed.

### Added endpoint
- `POST /api/v1/recalc_collective` — triggers recalculation of collective truth scores in the Core.

Request body: none

Response:
```json
{ "status": "ok" }
```

### Client changes
- Model: `TruthEvent.collectiveScore` is nullable and included in JSON (Gson `@SerializedName("collective_score")`).
- API: Retrofit interface exposes `recalcCollective()` returning `ApiStatus`.
- UI: Event card shows "Collective Truth" as percentage and a progress bar (0–100%).
- P2P Sync: When exchanging events, `collective_score` is included in JSON; merges use weighted average
  `merged = ((localCount * localScore) + (remoteCount * remoteScore)) / (localCount + remoteCount)` when both sides have values.

### Distributed evaluation and the Collective Intelligence Layer
Truth Core aggregates local evaluations from peers into a global (collective) score. The mobile client surfaces this metric and can request a full recomputation via the new endpoint. During P2P synchronization, partial aggregates can be merged via weighted averaging to converge across peers.
