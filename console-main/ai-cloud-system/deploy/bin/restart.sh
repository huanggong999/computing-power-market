#!/usr/bin/env bash
# 重启 ai-cloud-system

set -eu

DIR="$(dirname "$0")"
"${DIR}/stop.sh"
"${DIR}/start.sh"
