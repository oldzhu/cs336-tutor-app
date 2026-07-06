# Feature: Full Code Review Component

**Status**: Planned · Not Started  
**Created**: 2026-07-06

## Problem
Users learn individual components in isolation but can't see the full assembled code. After completing all 10 components, there's no way to review the complete working assignment.

## Solution
Add a "Full Code Review" component to the Dashboard that assembles all component code into a single view and allows AI-powered review.

### Features
- **Code Assembly**: Combine all 10 component codes into one complete file
- **AI Review**: Send assembled code to DeepSeek for comprehensive review
- **Line References**: Each code section tagged with its source component
- **Copy/Share**: Export full code to clipboard

### Dashboard Entry
- Fake/locked component card at the bottom of Dashboard
- Unlocks when all components are completed (progress tracking)
- Or: always available as "Review Assignment" option

### LLM Prompt
```
Review this complete CS336 Assignment 1 implementation:
[BPE code]
[Embedding code]
...
[Training loop code]

Evaluate correctness, style, performance, and completeness.
```

### Dependencies
- All 10 component specs (✅ done)
- LLM provider (✅ done)
- Progress tracking (P2, optional unlock trigger)
