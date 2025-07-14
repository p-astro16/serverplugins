#!/usr/bin/env python3
"""
Product Scraping System with Fallback Methods
==============================================

This script attempts to scrape Minecraft product/item information from multiple sources.
When primary methods fail, it uses fallback approaches to ensure data collection.
"""

import requests
import json
import time
import logging
from typing import Dict, List, Optional, Any
from dataclasses import dataclass
from enum import Enum
import subprocess
import re

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='[%(asctime)s] %(levelname)s: %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S'
)
logger = logging.getLogger(__name__)

class ScrapeMethod(Enum):
    PRIMARY_API = "primary_api"
    FALLBACK_API = "fallback_api"
    LOCAL_CACHE = "local_cache"
    GAME_DATA = "game_data"
    COMMUNITY_DB = "community_db"
    DEFAULT_VALUES = "default_values"

@dataclass
class ProductInfo:
    id: str
    name: str
    description: str
    rarity: str
    max_stack_size: int
    tradeable: bool
    value: float
    source_method: ScrapeMethod
    last_updated: str

class ProductScraper:
    def __init__(self):
        self.primary_api_url = "https://api.minecraft-items.com/v1"
        self.fallback_api_url = "https://backup-api.minecraft-data.net/v2"
        self.community_db_url = "https://community.minecraft-trading.org/api"
        self.cache_file = "product_cache.json"
        self.failed_products = []
        self.scraped_products = {}
        
        # Load existing cache
        self.load_cache()
    
    def load_cache(self):
        """Load previously cached product data"""
        try:
            with open(self.cache_file, 'r') as f:
                cache_data = json.load(f)
                logger.info(f"Loaded {len(cache_data)} products from cache")
                return cache_data
        except FileNotFoundError:
            logger.info("No cache file found, starting fresh")
            return {}
        except Exception as e:
            logger.error(f"Error loading cache: {e}")
            return {}
    
    def save_cache(self, products: Dict[str, ProductInfo]):
        """Save product data to cache"""
        try:
            cache_data = {}
            for product_id, product in products.items():
                cache_data[product_id] = {
                    'id': product.id,
                    'name': product.name,
                    'description': product.description,
                    'rarity': product.rarity,
                    'max_stack_size': product.max_stack_size,
                    'tradeable': product.tradeable,
                    'value': product.value,
                    'source_method': product.source_method.value,
                    'last_updated': product.last_updated
                }
            
            with open(self.cache_file, 'w') as f:
                json.dump(cache_data, f, indent=2)
                logger.info(f"Saved {len(cache_data)} products to cache")
        except Exception as e:
            logger.error(f"Error saving cache: {e}")
    
    def scrape_product_primary(self, product_id: str) -> Optional[ProductInfo]:
        """Primary scraping method using main API"""
        try:
            logger.info(f"Attempting to scrape product: {product_id}")
            response = requests.get(
                f"{self.primary_api_url}/items/{product_id}",
                timeout=10,
                headers={'User-Agent': 'MinecraftProductScraper/1.0'}
            )
            
            if response.status_code == 200:
                data = response.json()
                product = ProductInfo(
                    id=data.get('id', product_id),
                    name=data.get('name', product_id.replace('_', ' ').title()),
                    description=data.get('description', ''),
                    rarity=data.get('rarity', 'common'),
                    max_stack_size=data.get('maxStackSize', 64),
                    tradeable=data.get('tradeable', True),
                    value=data.get('value', 1.0),
                    source_method=ScrapeMethod.PRIMARY_API,
                    last_updated=time.strftime('%Y-%m-%d %H:%M:%S')
                )
                logger.info(f"Successfully scraped {product_id} using primary method")
                return product
            else:
                logger.error(f"Failed to scrape {product_id} - HTTP {response.status_code}")
                return None
                
        except requests.exceptions.Timeout:
            logger.error(f"Failed to scrape {product_id} - Connection timeout to primary source")
            return None
        except requests.exceptions.ConnectionError:
            logger.error(f"Failed to scrape {product_id} - DNS resolution failed")
            return None
        except json.JSONDecodeError:
            logger.error(f"Failed to scrape {product_id} - Invalid JSON response")
            return None
        except Exception as e:
            logger.error(f"Failed to scrape {product_id} - {str(e)}")
            return None
    
    def scrape_product_fallback_api(self, product_id: str) -> Optional[ProductInfo]:
        """Fallback method using alternative API"""
        try:
            logger.info(f"Trying fallback API for {product_id}")
            response = requests.get(
                f"{self.fallback_api_url}/minecraft/items/{product_id}",
                timeout=15,
                headers={'User-Agent': 'MinecraftProductScraper/1.0'}
            )
            
            if response.status_code == 200:
                data = response.json()
                product = ProductInfo(
                    id=product_id,
                    name=data.get('displayName', product_id.replace('_', ' ').title()),
                    description=data.get('description', 'Retrieved from fallback API'),
                    rarity=data.get('rarity', 'common'),
                    max_stack_size=data.get('stackSize', 64),
                    tradeable=True,
                    value=data.get('estimatedValue', 1.0),
                    source_method=ScrapeMethod.FALLBACK_API,
                    last_updated=time.strftime('%Y-%m-%d %H:%M:%S')
                )
                logger.info(f"Successfully scraped {product_id} using fallback API")
                return product
            else:
                logger.warning(f"Fallback API failed for {product_id} - HTTP {response.status_code}")
                return None
                
        except Exception as e:
            logger.warning(f"Fallback API error for {product_id}: {e}")
            return None
    
    def scrape_product_community_db(self, product_id: str) -> Optional[ProductInfo]:
        """Community database fallback method"""
        try:
            logger.info(f"Trying community database for {product_id}")
            response = requests.get(
                f"{self.community_db_url}/items",
                params={'search': product_id},
                timeout=20
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get('results'):
                    item_data = data['results'][0]
                    product = ProductInfo(
                        id=product_id,
                        name=item_data.get('name', product_id.replace('_', ' ').title()),
                        description=item_data.get('description', 'Retrieved from community database'),
                        rarity=item_data.get('rarity', 'common'),
                        max_stack_size=item_data.get('maxStack', 64),
                        tradeable=item_data.get('tradeable', True),
                        value=item_data.get('avgPrice', 1.0),
                        source_method=ScrapeMethod.COMMUNITY_DB,
                        last_updated=time.strftime('%Y-%m-%d %H:%M:%S')
                    )
                    logger.info(f"Successfully scraped {product_id} using community database")
                    return product
            
        except Exception as e:
            logger.warning(f"Community database error for {product_id}: {e}")
            return None
        
        return None
    
    def scrape_product_from_game_data(self, product_id: str) -> Optional[ProductInfo]:
        """Extract data from local Minecraft installation or jar files"""
        try:
            logger.info(f"Trying game data extraction for {product_id}")
            
            # This would normally extract from Minecraft JAR files or data files
            # For demonstration, we'll simulate with predefined data
            game_items = {
                'minecraft:diamond_sword': {
                    'name': 'Diamond Sword',
                    'description': 'A powerful sword made of diamond',
                    'rarity': 'rare',
                    'maxStack': 1,
                    'value': 100.0
                },
                'minecraft:enchanted_book': {
                    'name': 'Enchanted Book',
                    'description': 'A book imbued with magical properties',
                    'rarity': 'uncommon',
                    'maxStack': 1,
                    'value': 50.0
                },
                'minecraft:netherite_ingot': {
                    'name': 'Netherite Ingot',
                    'description': 'The strongest material in Minecraft',
                    'rarity': 'legendary',
                    'maxStack': 64,
                    'value': 500.0
                }
            }
            
            full_id = product_id if product_id.startswith('minecraft:') else f'minecraft:{product_id}'
            
            if full_id in game_items:
                data = game_items[full_id]
                product = ProductInfo(
                    id=product_id,
                    name=data['name'],
                    description=data['description'],
                    rarity=data['rarity'],
                    max_stack_size=data['maxStack'],
                    tradeable=True,
                    value=data['value'],
                    source_method=ScrapeMethod.GAME_DATA,
                    last_updated=time.strftime('%Y-%m-%d %H:%M:%S')
                )
                logger.info(f"Successfully extracted {product_id} from game data")
                return product
            
        except Exception as e:
            logger.warning(f"Game data extraction error for {product_id}: {e}")
            return None
        
        return None
    
    def generate_default_product(self, product_id: str) -> ProductInfo:
        """Last resort: generate product with default values"""
        logger.info(f"Generating default product data for {product_id}")
        
        # Infer basic properties from product ID
        name = product_id.replace('minecraft:', '').replace('_', ' ').title()
        
        # Determine rarity based on name patterns
        rarity = 'common'
        value = 1.0
        max_stack = 64
        
        if any(word in product_id.lower() for word in ['diamond', 'netherite', 'dragon']):
            rarity = 'rare'
            value = 100.0
        elif any(word in product_id.lower() for word in ['enchanted', 'golden', 'emerald']):
            rarity = 'uncommon'
            value = 25.0
        elif any(word in product_id.lower() for word in ['sword', 'bow', 'trident']):
            max_stack = 1
            value = 10.0
        
        return ProductInfo(
            id=product_id,
            name=name,
            description=f'Default information for {name}',
            rarity=rarity,
            max_stack_size=max_stack,
            tradeable=True,
            value=value,
            source_method=ScrapeMethod.DEFAULT_VALUES,
            last_updated=time.strftime('%Y-%m-%d %H:%M:%S')
        )
    
    def scrape_product_with_fallbacks(self, product_id: str) -> ProductInfo:
        """
        Scrape a product using multiple fallback methods
        This is the main method that implements the requirement
        """
        logger.info(f"Starting comprehensive scrape for {product_id}")
        
        # Method 1: Primary API
        result = self.scrape_product_primary(product_id)
        if result:
            return result
        
        # Method 2: Fallback API
        logger.info(f"Primary method failed for {product_id}, trying fallback API")
        result = self.scrape_product_fallback_api(product_id)
        if result:
            return result
        
        # Method 3: Community Database
        logger.info(f"Fallback API failed for {product_id}, trying community database")
        result = self.scrape_product_community_db(product_id)
        if result:
            return result
        
        # Method 4: Game Data Extraction
        logger.info(f"Community database failed for {product_id}, trying game data extraction")
        result = self.scrape_product_from_game_data(product_id)
        if result:
            return result
        
        # Method 5: Default Values (always succeeds)
        logger.warning(f"All methods failed for {product_id}, using default values")
        return self.generate_default_product(product_id)
    
    def scrape_products_from_log(self, log_file: str) -> Dict[str, ProductInfo]:
        """
        Parse the log file to extract failed products and scrape them with fallbacks
        """
        failed_products = []
        
        try:
            with open(log_file, 'r') as f:
                content = f.read()
                
            # Extract product IDs from log file
            lines = content.split('\n')
            for line in lines:
                if 'Product ID:' in line and 'Primary scraping failed' in line:
                    match = re.search(r'Product ID: ([^\s]+)', line)
                    if match:
                        product_id = match.group(1)
                        failed_products.append(product_id)
            
            logger.info(f"Found {len(failed_products)} failed products in log file")
            
        except Exception as e:
            logger.error(f"Error reading log file: {e}")
            return {}
        
        # Scrape each failed product with fallback methods
        scraped_products = {}
        for product_id in failed_products:
            try:
                product = self.scrape_product_with_fallbacks(product_id)
                scraped_products[product_id] = product
                logger.info(f"Successfully recovered {product_id} using {product.source_method.value}")
                
                # Small delay to avoid overwhelming servers
                time.sleep(1)
                
            except Exception as e:
                logger.error(f"Failed to recover {product_id}: {e}")
        
        return scraped_products
    
    def generate_report(self, products: Dict[str, ProductInfo]) -> str:
        """Generate a summary report of the scraping results"""
        report = []
        report.append("PRODUCT SCRAPING RECOVERY REPORT")
        report.append("=" * 50)
        report.append(f"Total products recovered: {len(products)}")
        report.append("")
        
        # Group by method
        method_counts = {}
        for product in products.values():
            method = product.source_method.value
            method_counts[method] = method_counts.get(method, 0) + 1
        
        report.append("Recovery methods used:")
        for method, count in method_counts.items():
            report.append(f"  - {method}: {count} products")
        
        report.append("")
        report.append("Recovered products:")
        report.append("-" * 30)
        
        for product in products.values():
            report.append(f"ID: {product.id}")
            report.append(f"  Name: {product.name}")
            report.append(f"  Rarity: {product.rarity}")
            report.append(f"  Value: {product.value}")
            report.append(f"  Method: {product.source_method.value}")
            report.append(f"  Updated: {product.last_updated}")
            report.append("")
        
        return "\n".join(report)

def main():
    """Main function to run the product scraper with fallbacks"""
    scraper = ProductScraper()
    
    # Parse the log file and recover failed products
    log_file = "pasted.txt"
    logger.info("Starting product recovery from failed scraping attempts")
    
    recovered_products = scraper.scrape_products_from_log(log_file)
    
    if recovered_products:
        # Save to cache
        scraper.save_cache(recovered_products)
        
        # Generate report
        report = scraper.generate_report(recovered_products)
        
        # Save report
        with open("recovery_report.txt", "w") as f:
            f.write(report)
        
        print(report)
        logger.info(f"Successfully recovered {len(recovered_products)} products using fallback methods")
        logger.info("Report saved to recovery_report.txt")
    else:
        logger.warning("No products were recovered")

if __name__ == "__main__":
    main()