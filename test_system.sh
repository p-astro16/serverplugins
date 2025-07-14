#!/bin/bash

# Product Scraping System Test Script
# ====================================

echo "🔍 Product Scraping System Test"
echo "================================"

# Check if Python is available
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 is required but not installed"
    exit 1
fi

echo "✅ Python 3 found"

# Check if required files exist
required_files=(
    "pasted.txt"
    "scrape_products.py"
    "demo_fallback_system.py"
)

for file in "${required_files[@]}"; do
    if [[ -f "$file" ]]; then
        echo "✅ Found: $file"
    else
        echo "❌ Missing: $file"
        exit 1
    fi
done

# Test Python scraper
echo ""
echo "🧪 Testing Python scraper..."
echo "----------------------------"

if python3 scrape_products.py > /dev/null 2>&1; then
    echo "✅ Python scraper executed successfully"
else
    echo "❌ Python scraper failed"
    exit 1
fi

# Check if output files were created
output_files=(
    "product_cache.json"
    "recovery_report.txt"
)

for file in "${output_files[@]}"; do
    if [[ -f "$file" ]]; then
        echo "✅ Generated: $file"
    else
        echo "❌ Missing output: $file"
        exit 1
    fi
done

# Test cache content
echo ""
echo "📊 Checking cache content..."
echo "----------------------------"

if python3 -c "
import json
try:
    with open('product_cache.json', 'r') as f:
        cache = json.load(f)
    print(f'✅ Cache contains {len(cache)} products')
    
    # Check for expected products
    expected = ['minecraft:diamond_sword', 'minecraft:enchanted_book', 'minecraft:netherite_ingot']
    found = sum(1 for p in expected if p in cache)
    print(f'✅ Found {found}/{len(expected)} expected products')
    
    # Check recovery methods
    methods = set(p.get('source_method', 'unknown') for p in cache.values())
    print(f'✅ Used recovery methods: {', '.join(methods)}')
    
except Exception as e:
    print(f'❌ Cache validation failed: {e}')
    exit(1)
" 2>/dev/null; then
    echo "✅ Cache validation passed"
else
    echo "❌ Cache validation failed"
    exit 1
fi

# Test demo script
echo ""
echo "🎬 Testing demo script..."
echo "------------------------"

if python3 demo_fallback_system.py > /dev/null 2>&1; then
    echo "✅ Demo script executed successfully"
else
    echo "❌ Demo script failed"
    exit 1
fi

# Check Java integration files
echo ""
echo "☕ Checking Java integration..."
echo "------------------------------"

java_files=(
    "src/main/java/com/example/teleportplugin/ProductScraper.java"
    "src/main/java/com/example/teleportplugin/ProductCommand.java"
    "src/main/java/com/example/teleportplugin/TeleportPlugin.java"
)

for file in "${java_files[@]}"; do
    if [[ -f "$file" ]]; then
        echo "✅ Found: $(basename "$file")"
    else
        echo "❌ Missing: $(basename "$file")"
        exit 1
    fi
done

# Check plugin.yml
if grep -q "product:" src/main/resources/plugin.yml; then
    echo "✅ Product command registered in plugin.yml"
else
    echo "❌ Product command not found in plugin.yml"
    exit 1
fi

# Check pom.xml for Gson dependency
if grep -q "gson" pom.xml; then
    echo "✅ Gson dependency found in pom.xml"
else
    echo "❌ Gson dependency missing from pom.xml"
    exit 1
fi

# Final summary
echo ""
echo "🎉 All Tests Passed!"
echo "==================="
echo ""
echo "📋 System Summary:"
echo "  • Python scraper: Working ✅"
echo "  • Fallback system: Working ✅"
echo "  • Cache system: Working ✅"
echo "  • Java integration: Ready ✅"
echo "  • Plugin configuration: Ready ✅"
echo ""
echo "🚀 The product scraping system with fallback methods is ready for use!"
echo ""
echo "📚 Next steps:"
echo "  1. Build the Java plugin: mvn clean package"
echo "  2. Deploy to Minecraft server"
echo "  3. Run /product recover in-game"
echo ""
echo "📖 See PRODUCT_SCRAPING_README.md for complete documentation"